package com.devanshu.lovableclone.service.impl;

import com.devanshu.lovableclone.config.properties.PanelUrlPropertiesConfig;
import com.devanshu.lovableclone.constant.SubscriptionStatus;
import com.devanshu.lovableclone.dto.subscription.CheckoutRequestDTO;
import com.devanshu.lovableclone.entity.Plan;
import com.devanshu.lovableclone.entity.Users;
import com.devanshu.lovableclone.exceptions.BadRequestException;
import com.devanshu.lovableclone.exceptions.NotFoundException;
import com.devanshu.lovableclone.repository.PlanRepository;
import com.devanshu.lovableclone.repository.UsersRepository;
import com.devanshu.lovableclone.security.JwtUserPrincipal;
import com.devanshu.lovableclone.service.HelperService;
import com.devanshu.lovableclone.service.PaymentProcessor;
import com.devanshu.lovableclone.service.SubscriptionService;
import com.devanshu.lovableclone.util.CommonUtility;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StripePaymentProcessor implements PaymentProcessor {
    private final UsersRepository usersRepository;

    HelperService helperService;
    PlanRepository planRepository;
    StripeClient stripeClient;
    PanelUrlPropertiesConfig panelUrlPropertiesConfig;
    SubscriptionService subscriptionService;

    @Override
    public String createPaymentCheckoutUrl(CheckoutRequestDTO checkoutRequestDTO) {
        JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
        Users users = helperService.getUser(jwtUserPrincipal.id());

        Plan plan = planRepository.findById(checkoutRequestDTO.planId())
                                  .orElseThrow(() -> new NotFoundException("Plan not found"));

        SessionCreateParams.Builder builder = SessionCreateParams
                .builder()
                .addLineItem(SessionCreateParams.LineItem.builder()
                                                         .setPrice(plan.getStripePriceId())
                                                         .setQuantity(1L)
                                                         .build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(new SessionCreateParams.SubscriptionData.Builder().setBillingMode(
                        SessionCreateParams.SubscriptionData.BillingMode.builder()
                                                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                                                        .build()).build()
                )
                .setSuccessUrl(panelUrlPropertiesConfig.getAdmin() + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(panelUrlPropertiesConfig.getAdmin() + "/cancel.html")
                .putMetadata("user_id", jwtUserPrincipal.id().toString())
                .putMetadata("plan_id", plan.getId().toString());
        try {
            String stripeCustomerId = users.getStripeCustomerId();
            if (StringUtils.hasText(stripeCustomerId)) {
                builder.setCustomer(stripeCustomerId);
            } else {
                builder.setCustomerEmail(users.getUsername());
            }
            Session session = stripeClient.v1().checkout().sessions().create(builder.build());
            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPaymentPortalUrl() {
        JwtUserPrincipal jwtUserPrincipal = helperService.checkForUserLogin();
        Users users = helperService.getUser(jwtUserPrincipal.id());
        String stripeCustomerId = users.getStripeCustomerId();

        if (!StringUtils.hasText(stripeCustomerId)) {
            throw new BadRequestException("User does not have a Stripe Customer Id, UserId:" + jwtUserPrincipal.id());
        }

        try {

            com.stripe.param.billingportal.SessionCreateParams params =
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                                                                      .setCustomerAccount(stripeCustomerId)
                                                                      .setReturnUrl(panelUrlPropertiesConfig.getAdmin())
                                                                      .build();

            com.stripe.model.billingportal.Session session = stripeClient.v1().billingPortal().sessions()
                                                                         .create(params);

            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.debug("Inside StripePaymentProcessor::handleWebhookEvent {}", type);

        switch (type) {
            case "checkout.session.completed" -> handleCheckoutSessionCompleted(stripeObject, metadata);
            case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated(stripeObject);
            case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted(stripeObject);
            case "invoice.paid" -> handleInvoicePaid(stripeObject);
            case "invoice.payment_failed" -> handleInvoicePaymentFailed(stripeObject);
            default -> log.debug("Ignored {}", type);
        }
    }

    /**
     * One time when checkout session completed
     */
    private void handleCheckoutSessionCompleted(StripeObject stripeObject, Map<String, String> metadata) {

        if (stripeObject instanceof Session session) {

            String userIdValue = metadata.get("user_id");
            String planIdValue = metadata.get("plan_id");

            if (userIdValue == null || planIdValue == null) {
                log.error("handleCheckoutSessionCompleted::Required metadata missing for session {}", session.getId());
                return;
            }

            Long userId = Long.parseLong(userIdValue);
            Long planId = Long.parseLong(planIdValue);

            String subscriptionId = session.getSubscription();
            String stripeCustomerId = session.getCustomer();

            Users users = helperService.getUser(userId);

            if (users.getStripeCustomerId() == null) {
                users.setStripeCustomerId(stripeCustomerId);
                usersRepository.save(users);
            }
            subscriptionService.activateSubscription(userId, planId, subscriptionId, stripeCustomerId);
        }
    }

    /**
     * when user cancels of upgrades or any other updates
     */
    private void handleCustomerSubscriptionUpdated(StripeObject stripeObject) {
        if (stripeObject instanceof Subscription subscription) {
            SubscriptionStatus status = SubscriptionStatus.toEnum(subscription.getStatus());
            if (status == null) {
                log.warn("handleCustomerSubscriptionUpdated::Unknown status '{}' for subscription {}", subscription.getStatus(), subscription.getId());
                return;
            }

            if (subscription.getItems().getData().isEmpty()) {
                log.warn("handleCustomerSubscriptionUpdated::No subscription items found for {}", subscription.getId());
                return;
            }

            SubscriptionItem item = subscription.getItems().getData().getFirst();
            Instant periodStart = CommonUtility.toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = CommonUtility.toInstant(item.getCurrentPeriodEnd());

            Long planId = resolvePlanId(item.getPrice());

            subscriptionService.updateSubscription(subscription.getId(), status, periodStart, periodEnd,
                    subscription.getCancelAtPeriodEnd(), planId);
        }
    }

    private Long resolvePlanId(Price price) {
        if (price == null || price.getId() == null) return null;

        return planRepository.findByStripePriceId(price.getId()).map(Plan::getId).orElse(null);
    }

    /**
     * when subscription ends, revoke the access
     */
    private void handleCustomerSubscriptionDeleted(StripeObject stripeObject) {
        if (stripeObject instanceof Subscription subscription) {
            subscriptionService.cancelSubscription(subscription.getId());
        }
    }

    /**
     * When invoice is paid
     */
    private void handleInvoicePaid(StripeObject stripeObject) {
        if (stripeObject instanceof Invoice invoice) {
            String subscriptionId = extractSubscriptionId(invoice);
            if (subscriptionId == null) return;

            try {
                Subscription subscription = stripeClient.v1().subscriptions()
                                                        .retrieve(subscriptionId); //sdk calling the Stripe server
                if (subscription.getItems().getData().isEmpty()) {
                    log.warn("handleInvoicePaid::No subscription items found for {}", subscription.getId());
                    return;
                }

                SubscriptionItem item = subscription.getItems().getData().getFirst();

                Instant periodStart = CommonUtility.toInstant(item.getCurrentPeriodStart());
                Instant periodEnd = CommonUtility.toInstant(item.getCurrentPeriodEnd());

                subscriptionService.renewSubscriptionPeriod(subscriptionId, periodStart, periodEnd);
            } catch (StripeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String extractSubscriptionId(Invoice invoice) {
        var parent = invoice.getParent();
        if (parent == null) return null;

        var subDetails = parent.getSubscriptionDetails();
        if (subDetails == null) return null;

        return subDetails.getSubscription();
    }

    /**
     * when invoice is not paid, mark as PAST_DUE
     */
    private void handleInvoicePaymentFailed(StripeObject stripeObject) {
        if (stripeObject instanceof Invoice invoice) {
            String subscriptionId = extractSubscriptionId(invoice);
            if (subscriptionId == null) return;

            subscriptionService.markSubscriptionPastDue(subscriptionId);
        }
    }

}
