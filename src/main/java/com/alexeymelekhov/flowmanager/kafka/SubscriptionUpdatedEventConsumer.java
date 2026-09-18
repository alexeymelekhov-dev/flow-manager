package com.alexeymelekhov.flowmanager.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionUpdatedEventConsumer {

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(
            topics = KafkaTopics.SUBSCRIPTION_UPDATED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String login) {
        redisTemplate.delete("subscription:" + login);

        log.info("Cache subscription deleted: subscription:{}", login);
    }
}
