package com.nextstep.api.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.form.BaseSendMsgForm;
import com.nextstep.api.model.Post;
import com.nextstep.api.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class RabbitMQListener {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @RabbitListener(queues = "${rabbitmq.queue.complete-process-cv}")
    public void handleListenCompleteProcessEmbeddingCv(String json) {
        try {
            // 1. Parse JSON thành BaseSendMsgForm<Map<String,Object>>
            BaseSendMsgForm<Map<String, Object>> form =
                    objectMapper.readValue(
                            json,
                            new TypeReference<BaseSendMsgForm<Map<String, Object>>>() {}
                    );

            // 2. Kiểm tra đúng cmd/subCmd COMPLETE_PROCESS_CV
            if (NextStepConstant.PROCESS_EMBEDDING.equals(form.getCmd()) &&
                    NextStepConstant.PROCESS_EMBEDDING.equals(form.getSubCmd())) {

                // 3. Lấy postId và cập nhật state
                Long postId = ((Number) form.getData().get("postId")).longValue();
                Post post = postRepository.findById(postId).orElse(null);
                if(post == null){
                    throw new BadRequestException("Post not found", ErrorCode.POST_ERROR_NOT_FOUND);
                }
                    post.setState(NextStepConstant.POST_EMBEDDING_STATE_DONE);
                    postRepository.save(post);
            }
        } catch (JsonProcessingException e) {
            log.error("can not parse COMPLETE_PROCESS_CV message", e);
        }
    }
}
