package com.alexeymelekhov.flowmanager.service;

import com.alexeymelekhov.flowmanager.client.SubscriptionClient;
import com.alexeymelekhov.flowmanager.dto.SubscriptionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SubscriptionCacheService {

    private final SubscriptionClient subscriptionClient;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public SubscriptionDTO getSubscription(String login) {
        String key = "subscription:" + login;

        String json = redisTemplate.opsForValue().get(key);

        if (json != null) {
            return objectMapper.readValue(json, SubscriptionDTO.class);
        }

        SubscriptionDTO subscription = subscriptionClient.getSubscription(login);

        redisTemplate.opsForValue().set(
                key,
                objectMapper.writeValueAsString(subscription)
        );

        return subscription;
    }
}
