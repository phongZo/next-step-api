package com.nextstep.api.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class RabbitMQListener {
    @RabbitListener(queues = "${rabbitmq.queue.data-embedding}")
    public void handleListenDataEmbedding(String json) {
        System.out.println("handleListenDataEmbedding");
    }
}
