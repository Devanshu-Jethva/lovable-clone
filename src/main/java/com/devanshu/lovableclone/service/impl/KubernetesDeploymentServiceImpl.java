package com.devanshu.lovableclone.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.devanshu.lovableclone.constant.ApplicationConstants;
import com.devanshu.lovableclone.dto.deploy.DeployResponseDTO;
import com.devanshu.lovableclone.exceptions.DeploymentException;
import com.devanshu.lovableclone.service.DeploymentService;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KubernetesDeploymentServiceImpl implements DeploymentService {

	private final KubernetesClient client;
	private final StringRedisTemplate redisTemplate;

	@Override
	public DeployResponseDTO deploy(final Long projectId) {

		String domain = "project-" + projectId + ".app.domain.com";

		Pod existingPod = findActivePod(projectId);

		if (existingPod != null) {
			registerRoute(domain, existingPod);
			return new DeployResponseDTO("http://" + domain + ":" + ApplicationConstants.REVERSE_PROXY_PORT);
		}

		return claimAndStartNewPod(projectId, domain);
	}

	private DeployResponseDTO claimAndStartNewPod(final Long projectId, final String domain) {
		Pod pod = client.pods().inNamespace(ApplicationConstants.NAMESPACE)
				.withLabel(ApplicationConstants.POOL_LABEL, ApplicationConstants.IDLE).list().getItems().stream()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No idle runners available. Please scale up the runner-pool."));

		String podName = pod.getMetadata().getName();
		log.info("Claiming pod {} for project {}", podName, projectId);

		client.pods().inNamespace(ApplicationConstants.NAMESPACE).withName(podName).edit(p -> {
			p.getMetadata().getLabels().put(ApplicationConstants.POOL_LABEL, ApplicationConstants.BUSY);
			p.getMetadata().getLabels().put(ApplicationConstants.PROJECT_LABEL, projectId.toString());
			return p;
		});

		try {
			// 1. Initial synchronization (synchronous)
			String initialSyncCmd = String.format("mc mirror --overwrite myminio/projects/%d/ /app/", projectId);
			execCommand(podName, ApplicationConstants.SYNCER_CONTAINER, "sh", "-c", initialSyncCmd);

			// 2. Continuous synchronization (background)
			String watchCmd = String.format("nohup mc mirror --overwrite --watch myminio/projects/%d/ /app/ "
					+ "</dev/null > /app/sync.log 2>&1 &", projectId);
			execBackgroundCommand(podName, ApplicationConstants.SYNCER_CONTAINER, "sh", "-c", watchCmd);

			// 3. Install dependencies (synchronous)
			execCommand(podName, ApplicationConstants.RUNNER_CONTAINER, "sh", "-c", "npm install");

			// 4. Start Vite (background)
			String startCmd = "nohup npm run dev -- --host 0.0.0.0 --port 5173 " + "</dev/null > /app/dev.log 2>&1 &";
			execBackgroundCommand(podName, ApplicationConstants.RUNNER_CONTAINER, "sh", "-c", startCmd);

			registerRoute(domain, pod);

			log.info("Deployment successful: http://{}:{}", domain, ApplicationConstants.REVERSE_PROXY_PORT);
			return new DeployResponseDTO("http://" + domain + ":" + ApplicationConstants.REVERSE_PROXY_PORT);

		} catch (Exception e) {
			log.error("Deployment failed for project {}. Releasing pod {}.", projectId, podName, e);
			client.pods().inNamespace(ApplicationConstants.NAMESPACE).withName(podName).delete();
			throw new DeploymentException("Failed to deploy the project with id: " + projectId);
		}
	}

	private void execCommand(final String podName, final String container, final String... command) {
		execCommand(podName, container, true, 10, TimeUnit.MINUTES, command);
	}

	private void execBackgroundCommand(final String podName, final String container, final String... command) {
		execCommand(podName, container, false, 0, TimeUnit.SECONDS, command);
	}

	private void execCommand(final String podName, final String container, final boolean waitForCompletion,
			final long timeout, final TimeUnit timeUnit, final String... command) {

		log.debug("Exec in {}:{} -> {}", podName, container, String.join(" ", command));

		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();

		try (ExecWatch watch = client.pods().inNamespace(ApplicationConstants.NAMESPACE).withName(podName)
				.inContainer(container).writingOutput(stdout).writingError(stderr).exec(command)) {

			if (!waitForCompletion) {
				Thread.sleep(500);
				return;
			}

			// Actual process exit code, NOT WebSocket close status.
			int exitCode = watch.exitCode().get(timeout, timeUnit);

			String output = stdout.toString(java.nio.charset.StandardCharsets.UTF_8);
			String error = stderr.toString(java.nio.charset.StandardCharsets.UTF_8);

			if (!output.isBlank()) {
				log.debug("Command output: {}", output);
			}

			if (exitCode != 0) {
				throw new DeploymentException(
						"Command failed with exit code: " + exitCode + ", stderr: " + error + ", stdout: " + output);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new DeploymentException("Pod execution interrupted", e);
		} catch (Exception e) {
			log.error("Exec failed in {}:{}", podName, container, e);
			throw new DeploymentException("Pod Execution Failed", e);
		}
	}

	private void registerRoute(final String domain, final Pod pod) {
		String podIp = pod.getStatus().getPodIP();
		if (podIp == null) {
			throw new RuntimeException("Pod is running but has no IP!");
		}

		// e.g.
		// route:project-1.app.domain.com -> 10.47.85.56:5173
		redisTemplate.opsForValue().set("route:" + domain, podIp + ":5173", 6, TimeUnit.HOURS);
	}

	Pod findActivePod(final Long projectId) {
		return client.pods().inNamespace(ApplicationConstants.NAMESPACE)
				.withLabel(ApplicationConstants.PROJECT_LABEL, projectId.toString())
				.withLabel(ApplicationConstants.POOL_LABEL, ApplicationConstants.BUSY) // Only find active/busy ones
				.list().getItems().stream().filter(pod -> pod.getStatus().getPhase().equals("Running")).findFirst()
				.orElse(null);
	}

}
