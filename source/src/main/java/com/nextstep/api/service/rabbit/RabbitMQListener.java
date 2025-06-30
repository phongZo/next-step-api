package com.nextstep.api.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.form.BaseSendMsgForm;
import com.nextstep.api.model.Candidate;
import com.nextstep.api.model.Post;
import com.nextstep.api.repository.CandidateRepository;
import com.nextstep.api.repository.PostRepository;
import com.nextstep.api.service.FileService;
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
    private CandidateRepository candidateRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FileService fileService;
    
    @RabbitListener(queues = "${rabbitmq.queue.complete-process-cv}")
    public void handleListenCompleteProcessEmbeddingCv(String json) {
        try {

            BaseSendMsgForm<Map<String, Object>> form =
                    objectMapper.readValue(json, new TypeReference<>() {});

            String cmd          = form.getCmd();
            String responseCode = form.getResponseCode();
            boolean success     = NextStepConstant.RESPONSE_CODE_SUCCESS.equals(responseCode);

            if (NextStepConstant.PROCESS_EMBEDDING.equals(cmd)) {

                Long postId = ((Number) form.getData().get("postId")).longValue();
                Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new BadRequestException(
                                "Post not found", ErrorCode.POST_ERROR_NOT_FOUND));


                post.setState(
                        success
                                ? NextStepConstant.POST_EMBEDDING_STATE_DONE
                                : NextStepConstant.POST_EMBEDDING_STATE_ERROR
                );
                postRepository.save(post);

            } else if (NextStepConstant.EXTRACT_CV.equals(cmd)) {

                Long candidateId = ((Number) form.getData().get("candidateId")).longValue();
                Candidate candidate = candidateRepository.findById(candidateId)
                        .orElseThrow(() -> new BadRequestException(
                                "Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND));
                candidate.setCvState(
                        success
                                ? NextStepConstant.CV_EMBEDDING_STATE_DONE
                                : NextStepConstant.CV_EMBEDDING_STATE_ERROR
                );
                if (candidate.getCv() != null && !candidate.getCv().isEmpty()) {
                    String newCvPath = fileService.moveCvToPermanentFolder(candidate.getId(), candidate.getCv());
                    candidate.setCv(newCvPath);
                }
                
                candidateRepository.save(candidate);
            } else {
                log.warn("Ignored unknown cmd: {}", cmd);
            }

        } catch (JsonProcessingException e) {
            log.error("Cannot parse COMPLETE_PROCESS_CV message", e);
        }
    }

}
