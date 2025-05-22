package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.dto.ResponseListDto;
import com.nextstep.api.dto.candidate.CandidateAdminDto;
import com.nextstep.api.dto.candidate.CandidateDto;
import com.nextstep.api.dto.candidate.GoogleUserInfo;
import com.nextstep.api.dto.candidate.GoogleVerifyDto;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.form.candidate.CandidateSignupForm;
import com.nextstep.api.form.candidate.GoogleRegisterForm;
import com.nextstep.api.form.candidate.GoogleVerifyForm;
import com.nextstep.api.form.candidate.UpdateCandidateProfileForm;
import com.nextstep.api.mapper.CandidateMapper;
import com.nextstep.api.model.Account;
import com.nextstep.api.model.Candidate;
import com.nextstep.api.model.Group;
import com.nextstep.api.model.criteria.CandidateCriteria;
import com.nextstep.api.repository.AccountRepository;
import com.nextstep.api.repository.CandidateRepository;
import com.nextstep.api.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/candidate")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CandidateController extends ABasicController{

    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    GroupRepository groupRepository;
    
    @Autowired
    CandidateRepository candidateRepository;

    @Autowired
    CandidateMapper candidateMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${google.userinfo.url}")
    private String googleUserInfoUrl;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAN_L')")
    public ApiMessageDto<ResponseListDto<List<CandidateAdminDto>>> getCandidateList(
            CandidateCriteria candidateCriteria,
            Pageable pageable
    ) {
        Specification<Candidate> specification = candidateCriteria.getSpecification();
        Page<Candidate> page = candidateRepository.findAll(specification, pageable);

        ResponseListDto<List<CandidateAdminDto>> responseListDto = new ResponseListDto<>(
                candidateMapper.fromEntitiesToCandidateAdminDtoList(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<CandidateAdminDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get candidate list successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAN_V')")
    public ApiMessageDto<CandidateAdminDto> getCandidate(@PathVariable Long id) {
        ApiMessageDto<CandidateAdminDto> apiMessageDto = new ApiMessageDto<>();
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found",  ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(candidateMapper.fromEntityToCandidateAdminDto(candidate));
        apiMessageDto.setMessage("Get candidate successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/signup", produces= MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ApiMessageDto<String> signUpCandidate(@Valid @RequestBody CandidateSignupForm candidateSignupForm, BindingResult bindingResult)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        if (candidateSignupForm.getPhone() != null && !candidateSignupForm.getPhone().isEmpty()) {
            if(accountRepository.existsByPhone(candidateSignupForm.getPhone())){
                throw new BadRequestException("Phone number already in use", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
            }
        }

        Group group = groupRepository.findFirstByKind(NextStepConstant.GROUP_KIND_CANDIDATE);
        if(group == null){
            throw new BadRequestException("Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND);
        }

        Account account = new Account();
        account.setKind(NextStepConstant.USER_KIND_CANDIDATE);
        account.setUsername(null);
        account.setPassword(passwordEncoder.encode(candidateSignupForm.getPassword()));
        account.setPhone(candidateSignupForm.getPhone());
        account.setEmail(candidateSignupForm.getEmail());
        account.setFullName(candidateSignupForm.getFullName());
        account.setGroup(group);
        account.setStatus(NextStepConstant.STATUS_ACTIVE);
        Account savedAccount = accountRepository.save(account);

        Candidate candidate = new Candidate();
        candidate.setAccount(savedAccount);
        candidateRepository.save(candidate);
        
        apiMessageDto.setMessage("Sign Up Success");
        return apiMessageDto;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<CandidateDto> getProfile() {
        ApiMessageDto<CandidateDto> apiMessageDto = new ApiMessageDto<>();

        Long accountId = getCurrentUser();
        Candidate candidate = candidateRepository.findById(accountId).orElse(null);
        if (candidate == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Candidate not found");
            return apiMessageDto;
        }
        apiMessageDto.setData(candidateMapper.fromEntityToCandidateDto(candidate));
        apiMessageDto.setMessage("Get candidate profile successfully");

        return apiMessageDto;
    }

    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ApiMessageDto<String> updateProfile(@Valid @RequestBody UpdateCandidateProfileForm updateCandidateProfileForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Long candidateId = getCurrentUser();

        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }

        Account account = candidate.getAccount();

        if (StringUtils.isNoneBlank(updateCandidateProfileForm.getPassword())
                || StringUtils.isNoneBlank(updateCandidateProfileForm.getOldPassword())) {

            if (!StringUtils.isNoneBlank(updateCandidateProfileForm.getOldPassword())) {
                throw new BadRequestException("Old password can not be empty", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
            }
            if (!StringUtils.isNoneBlank(updateCandidateProfileForm.getPassword())) {
                throw new BadRequestException("New password can not be empty", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
            }
            if (!passwordEncoder.matches(updateCandidateProfileForm.getOldPassword(), account.getPassword())) {
                throw new BadRequestException("Old password is not correct", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
            }
            if (!passwordEncoder.matches(updateCandidateProfileForm.getPassword(), account.getPassword())) {
                account.setPassword(passwordEncoder.encode(updateCandidateProfileForm.getPassword()));
            }
        }

        account.setFullName(updateCandidateProfileForm.getFullName());

        if (updateCandidateProfileForm.getPassword() != null && !updateCandidateProfileForm.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(updateCandidateProfileForm.getPassword()));
        }

        accountRepository.save(account);

        candidateMapper.updateFromUpdateCandidateProfileForm(candidate, updateCandidateProfileForm);

        candidateRepository.save(candidate);
        
        apiMessageDto.setMessage("Update profile successfully");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAN_D')")
    @Transactional
    public ApiMessageDto<String> deleteCandidate(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        
        Candidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }
        Account account = candidate.getAccount();
        if (account != null) {
            account.setStatus(NextStepConstant.STATUS_DELETE);
            accountRepository.save(account);
        }
        candidateRepository.delete(candidate);
        apiMessageDto.setMessage("Delete candidate successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/google-verify", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ApiMessageDto<GoogleVerifyDto> googleVerify(GoogleVerifyForm googleVerifyForm) {
        ApiMessageDto<GoogleVerifyDto> apiMessageDto = new ApiMessageDto<>();

        RestTemplate restTemplate = new RestTemplate();
        String url = googleUserInfoUrl + "?access_token=" + googleVerifyForm.getAccessToken();
        GoogleUserInfo userInfo;
        try {
            userInfo = restTemplate.getForObject(url, GoogleUserInfo.class);
        } catch (Exception e) {
            throw new BadRequestException("Invalid Google access token", ErrorCode.ACCOUNT_ERROR_TOKEN_INVALID);
        }
        if (userInfo == null || userInfo.email == null) {
            throw new BadRequestException("Cannot get user info from Google", ErrorCode.ACCOUNT_ERROR_TOKEN_INVALID);
        }

        Account account = accountRepository.findAccountByEmail(userInfo.email);
        Candidate candidate = null;
        boolean isNew = false;
        if (account == null) {
            Group group = groupRepository.findFirstByKind(NextStepConstant.GROUP_KIND_CANDIDATE);
            if (group == null) {
                throw new BadRequestException("Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND);
            }
            account = new Account();
            account.setKind(NextStepConstant.USER_KIND_CANDIDATE);
            account.setUsername(null);
            account.setPassword("");
            account.setPhone(null);
            account.setEmail(userInfo.email);
            account.setFullName(userInfo.name);
            account.setGroup(group);
            account.setStatus(NextStepConstant.STATUS_ACTIVE);
            account = accountRepository.save(account);

            candidate = new Candidate();
            candidate.setAccount(account);
            candidate = candidateRepository.save(candidate);
            isNew = true;
        } else {
            candidate = candidateRepository.findById(account.getId()).orElse(null);
            if (candidate == null) {
                candidate = new Candidate();
                candidate.setAccount(account);
                candidate = candidateRepository.save(candidate);
            }
        }

        GoogleVerifyDto response = new GoogleVerifyDto();
        response.candidate = candidateMapper.fromEntityToCandidateDto(candidate);
        response.isNew = isNew;
        apiMessageDto.setData(response);
        apiMessageDto.setMessage("Google verify success");
        return apiMessageDto;
    }

    @PostMapping(value = "/google-register", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ApiMessageDto<CandidateDto> googleRegister(@Valid @RequestBody GoogleRegisterForm googleRegisterForm) {
        ApiMessageDto<CandidateDto> apiMessageDto = new ApiMessageDto<>();

        Candidate candidate = candidateRepository.findById(googleRegisterForm.getUserId()).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }
        Account account = candidate.getAccount();

        if (googleRegisterForm.getFullName() != null) {
            account.setFullName(googleRegisterForm.getFullName());
        }
        if (googleRegisterForm.getPhone() != null && !googleRegisterForm.getPhone().isEmpty()) {
            if(accountRepository.existsByPhone(googleRegisterForm.getPhone())){
                throw new BadRequestException("Phone number already in use", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
            }
        }
        account.setPhone(googleRegisterForm.getPhone());

        accountRepository.save(account);
        candidateRepository.save(candidate);

        apiMessageDto.setData(candidateMapper.fromEntityToCandidateDto(candidate));
        apiMessageDto.setMessage("Google register success");
        return apiMessageDto;
    }
}
