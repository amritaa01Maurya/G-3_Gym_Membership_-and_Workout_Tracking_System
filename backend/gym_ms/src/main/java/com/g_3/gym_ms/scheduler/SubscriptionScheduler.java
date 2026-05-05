package com.g_3.gym_ms.scheduler;

import com.g_3.gym_ms.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {
    
    private final SubscriptionService subscriptionService;
    
    /**
     * Scheduled task to mark expired subscriptions
     * Runs daily at 00:01 AM
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void markExpiredSubscriptions() {
        log.info("=== SCHEDULER START: Marking expired subscriptions ===");
        try {
            subscriptionService.markExpiredSubscriptions();
            log.info("=== SCHEDULER SUCCESS: Expired subscriptions marked successfully ===");
        } catch (Exception e) {
            log.error("=== SCHEDULER ERROR: Failed to mark expired subscriptions ===", e);
        }
    }
    
    /**
     * Alternative fixed delay execution (for testing)
     * Uncomment to use fixed delay instead of cron expression
     * This will run 5 minutes after application startup and then every 24 hours
     */
    /*
    @Scheduled(initialDelay = 300000, fixedDelay = 86400000)
    public void markExpiredSubscriptionsFixedDelay() {
        log.info("=== SCHEDULER START: Marking expired subscriptions (fixed delay) ===");
        try {
            subscriptionService.markExpiredSubscriptions();
            log.info("=== SCHEDULER SUCCESS: Expired subscriptions marked successfully ===");
        } catch (Exception e) {
            log.error("=== SCHEDULER ERROR: Failed to mark expired subscriptions ===", e);
        }
    }
    */
}
