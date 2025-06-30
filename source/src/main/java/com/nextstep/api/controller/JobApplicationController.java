package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.dto.ResponseListDto;
import com.nextstep.api.dto.jobapplication.JobApplicationDto;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.form.jobapplication.CreateJobApplicationForm;
import com.nextstep.api.form.jobapplication.ChangeJobApplicationStateForm;
import com.nextstep.api.jwt.NextStepJwt;
import com.nextstep.api.model.Candidate;
import com.nextstep.api.model.JobApplication;
import com.nextstep.api.model.Post;
import com.nextstep.api.model.criteria.JobApplicationCriteria;
import com.nextstep.api.repository.CandidateRepository;
import com.nextstep.api.repository.JobApplicationRepository;
import com.nextstep.api.repository.PostRepository;
import com.nextstep.api.mapper.JobApplicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/application")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class JobApplicationController extends ABasicController {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('JBA_L')")
    public ApiMessageDto<ResponseListDto<List<JobApplicationDto>>> getJobApplicationList(
            JobApplicationCriteria jobApplicationCriteria,
            Pageable pageable
    ) {
        Specification<JobApplication> specification = jobApplicationCriteria.getSpecification();
        Page<JobApplication> page = jobApplicationRepository.findAll(specification, pageable);
        ResponseListDto<List<JobApplicationDto>> responseListDto = new ResponseListDto<>(
                jobApplicationMapper.fromEntitiesToJobApplicationDtoList(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages()
        );
        
        ApiMessageDto<ResponseListDto<List<JobApplicationDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get job application list successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('JBA_V')")
    public ApiMessageDto<JobApplicationDto> getJobApplication(@PathVariable Long id) {
        ApiMessageDto<JobApplicationDto> apiMessageDto = new ApiMessageDto<>();
        JobApplication jobApplication = jobApplicationRepository.findById(id).orElse(null);
        if (jobApplication == null) {
            throw new BadRequestException("Job application not found", ErrorCode.JOB_APPLICATION_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(jobApplicationMapper.fromEntityToJobApplicationDto(jobApplication));
        apiMessageDto.setMessage("Get job application successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('JBA_C')")
    @Transactional
    public ApiMessageDto<String> createJobApplication(
            @Valid @RequestBody CreateJobApplicationForm createJobApplicationForm,
            BindingResult bindingResult
    ) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Long candidateId = getCurrentUser();
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }
        Post post = postRepository.findById(createJobApplicationForm.getPostId()).orElse(null);
        if (post == null) {
            throw new BadRequestException("Post not found",ErrorCode.POST_ERROR_NOT_FOUND);
        }
        JobApplication jobApplication = jobApplicationMapper.fromCreateFormJobApplicationToEntity(createJobApplicationForm);
        jobApplication.setCandidate(candidate);
        jobApplication.setPost(post);
        jobApplication.setState(NextStepConstant.JOB_APPLICATION_STATE_PENDING);
        jobApplicationRepository.save(jobApplication);
        apiMessageDto.setMessage("Create job application successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/change-state", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('JBA_U_STATE')")
    @Transactional
    public ApiMessageDto<String> changeJobApplicationState(
            @Valid @RequestBody ChangeJobApplicationStateForm changeJobApplicationStateForm,
            BindingResult bindingResult
    ) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        JobApplication jobApplication = jobApplicationRepository.findById(changeJobApplicationStateForm.getId()).orElse(null);
        if (jobApplication == null) {
            throw new BadRequestException("Job application not found", ErrorCode.JOB_APPLICATION_ERROR_NOT_FOUND);
        }
        jobApplication.setState(changeJobApplicationStateForm.getState());
        jobApplicationRepository.save(jobApplication);
        apiMessageDto.setMessage("Job application state changed successfully");
        return apiMessageDto;
    }
}
