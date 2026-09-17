package com.alexeymelekhov.flowmanager.kafka;

import com.alexeymelekhov.flowmanager.dto.FileConvertedEventDTO;
import com.alexeymelekhov.flowmanager.exception.ErrorMessage;
import com.alexeymelekhov.flowmanager.service.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileConvertedEventConsumer {

    private final ObjectMapper objectMapper;
    private final FileService fileService;

    @KafkaListener(
            topics = KafkaTopics.FILE_CONVERTED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message) {
        try {
            log.info("MESSAGE CONVERTED is " + message);

            FileConvertedEventDTO dto = objectMapper.readValue(message, FileConvertedEventDTO.class);

            fileService.handleFileConverted(dto);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(ErrorMessage.FAILED_DESERIALIZE_EVENT.getMessage(), e);
        }
    }
}
