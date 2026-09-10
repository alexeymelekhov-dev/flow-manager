package com.alexeymelekhov.flowmanager.kafka;

import com.alexeymelekhov.flowmanager.exception.ErrorMessage;
import com.alexeymelekhov.flowmanager.model.Outbox;
import com.alexeymelekhov.flowmanager.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadedEventProducer {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(name = "publishPendingEvents", lockAtMostFor = "30s", lockAtLeastFor = "1s")
    public void publishPendingEvents() {
        outboxRepository.findByPublishedAtIsNull(PageRequest.of(0, BATCH_SIZE))
                .forEach(this::publish);
    }

    private void publish(Outbox outbox) {
        try {
            kafkaTemplate.send(
                    outbox.getTopic(),
                    outbox.getPayload()
            ).get();

            outbox.setPublishedAt(OffsetDateTime.now());
            outboxRepository.save(outbox);
        } catch (Exception e) {
            log.error(ErrorMessage.FAILED_PUBLISH_EVENT.format(outbox.getEventId()), e);
        }
    }
}
