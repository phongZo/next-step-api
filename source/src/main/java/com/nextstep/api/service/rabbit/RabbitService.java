package com.nextstep.api.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.UploadFileDto;
import com.nextstep.api.form.BaseSendMsgForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RabbitService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private com.nextstep.api.service.rabbit.RabbitSender rabbitSender;
    @Value("${rabbitmq.queue.process-cv}")
    private String processCvQueue;

    public  <T> void handleSendMsg(String appName, String queueName, T data, String cmd, String subCmd,String responseCode, String token) {
        BaseSendMsgForm<T> form = new BaseSendMsgForm<>();
        form.setApp(appName);
        form.setCmd(cmd);
        form.setSubCmd(subCmd);
        form.setData(data);
        form.setResponseCode(responseCode);
        form.setToken(token);
        String msg;
        try {
            msg = objectMapper.writeValueAsString(form);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // create queue if existed
        createQueueIfNotExist(queueName);

        // push msg
        rabbitSender.send(queueName, msg);
    }
    public void processCvEmbeddingQueue(Long postId, String description, String token) {
        Map<String,Object> data = new HashMap<>();
        data.put("postId", postId);
        data.put("description", description);
        handleSendMsg(
                "nextstep-api",
                processCvQueue,
                data,
                NextStepConstant.PROCESS_EMBEDDING,
                NextStepConstant.PROCESS_EMBEDDING,
                "200",
                token
        );
    }

    public void send(UploadFileDto fileDto, Long candidateId) {

        Map<String, Object> message = new HashMap<>();
        message.put("filePath", fileDto.getFilePath());
        message.put("candidateId", candidateId);

        String msg;
        try {
            msg = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize CV upload payload", e);
            throw new RuntimeException("Could not serialize message", e);
        }

        createQueueIfNotExist(processCvQueue);

        rabbitSender.send(processCvQueue, msg);
    }

    private void createQueueIfNotExist(String queueName) {
        rabbitSender.createQueueIfNotExist(queueName);
    }


}
