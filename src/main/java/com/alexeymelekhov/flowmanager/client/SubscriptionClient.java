package com.alexeymelekhov.flowmanager.client;

import com.alexeymelekhov.flowmanager.dto.SubscriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/v1/subscriptions/{login}")
    SubscriptionDTO getSubscription(@PathVariable String login);
}
