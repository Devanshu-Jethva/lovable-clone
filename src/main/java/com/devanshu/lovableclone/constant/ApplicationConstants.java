package com.devanshu.lovableclone.constant;

public final class ApplicationConstants {

	private ApplicationConstants() {
	}

	public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	public static final Integer FREE_TIER_PROJECTS_ALLOWED = 100;

	public static final String TEMPLATE_BUCKET = "starter-projects";
	public static final String MAIN_BUCKET = "projects";
	public static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter";
	public static final String FILE_SEPARATOR = "/";

	// k8s
	public static final String NAMESPACE = "kind-lovable-clone-app";
	public static final String POOL_LABEL = "status";
	public static final String PROJECT_LABEL = "project-id";
	public static final String IDLE = "idle";
	public static final String BUSY = "busy";
	public static final String SYNCER_CONTAINER = "syncer";
	public static final String RUNNER_CONTAINER = "runner";
	public static final String REVERSE_PROXY_PORT = "8090";
}
