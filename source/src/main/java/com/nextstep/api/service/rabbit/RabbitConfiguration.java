package com.nextstep.api.service.rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
  /*  @Value("${rabbitmq.notification.queue}")
    private String notificationQueue;
    @Value("${rabbitmq.process-tables.queue}")
    private String processTableQueue;*/

    @Value("${rabbitmq.queue.cv-upload}")
    private String cvUploadQueue;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

   /* @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue, true);
    }

    @Bean
    public Queue processTableQueue() {
        return new Queue(processTableQueue, true);
    }*/

    @Bean
    public Queue uploadCvQueue() {
        return new Queue(cvUploadQueue, true);
    }
}
