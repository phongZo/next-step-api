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
import com.nextstep.api.form.candidate.*;
import com.nextstep.api.mapper.CandidateMapper;
import com.nextstep.api.model.Account;
import com.nextstep.api.model.Candidate;
import com.nextstep.api.model.Group;
import com.nextstep.api.model.Post;
import com.nextstep.api.model.criteria.CandidateCriteria;
import com.nextstep.api.repository.AccountRepository;
import com.nextstep.api.repository.CandidateRepository;
import com.nextstep.api.repository.GroupRepository;
import com.nextstep.api.repository.PostRepository;
import com.nextstep.api.service.FileService;
import com.nextstep.api.service.feign.GoogleFeignClient;
import com.nextstep.api.service.Oauth2JWTTokenService;
import com.nextstep.api.service.rabbit.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import com.nextstep.api.dto.post.PostDto;
import com.nextstep.api.mapper.PostMapper;

import javax.validation.Valid;
import java.util.ArrayList;
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

    @Autowired
    private GoogleFeignClient googleFeignClient;

    @Autowired
    private Oauth2JWTTokenService oauth2JWTTokenService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private FileService fileService;

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

        String code = com.nextstep.api.utils.StringUtils.generateRandomString(7);
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
        candidate.setCode(code);
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

        if (StringUtils.isNotBlank(updateCandidateProfileForm.getAvatar())) {
            if(!updateCandidateProfileForm.getAvatar().equals(account.getAvatarPath())){
                fileService.deleteFile(account.getAvatarPath());
            }
            account.setAvatarPath(updateCandidateProfileForm.getAvatar());
        }

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
        GoogleVerifyDto response = new GoogleVerifyDto();
        GoogleUserInfo userInfo;
        OAuth2AccessToken token = null;
        String resetCode = com.nextstep.api.utils.StringUtils.generateRandomString(6);

        try {
            userInfo = googleFeignClient.getUserInfo("Bearer " + googleVerifyForm.getAccessToken());
        } catch (Exception e) {
            throw new BadRequestException("Invalid Google access token", ErrorCode.ACCOUNT_ERROR_TOKEN_INVALID);
        }

        if (userInfo == null || userInfo.email == null) {
            throw new BadRequestException("Cannot get user info from Google", ErrorCode.ACCOUNT_ERROR_TOKEN_INVALID);
        }

        Account account = accountRepository.findByEmailAndPlatform(userInfo.email,NextStepConstant.ACCOUNT_PLATFORM_GOOGLE).orElse(null);
        if (account == null) {
            Group group = groupRepository.findFirstByKind(NextStepConstant.GROUP_KIND_CANDIDATE);
            if (group == null) {
                throw new BadRequestException("Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND);
            }
            account = new Account();
            account.setKind(NextStepConstant.USER_KIND_CANDIDATE);
            account.setEmail(userInfo.email);
            account.setGroup(group);
            account.setResetPwdCode(resetCode);
            account.setStatus(NextStepConstant.STATUS_PENDING);
            account.setPlatform(NextStepConstant.ACCOUNT_PLATFORM_GOOGLE);
            account = accountRepository.save(account);
            response.setPlatformUserId(account.getId());
            response.setCode(account.getResetPwdCode());
            response.setPlatform(account.getPlatform());
        } else {
            if (account.getStatus() == NextStepConstant.STATUS_ACTIVE) {
                token = oauth2JWTTokenService.getAccessTokenForCandidate(account.getEmail());
                if (token != null) {
                    response.setOAuth2AccessToken(token);
                }
            }
        }
        apiMessageDto.setData(response);
        apiMessageDto.setMessage("Google verify success");
        return apiMessageDto;
    }


    @PostMapping(value = "/google-register", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ApiMessageDto<CandidateDto> googleRegister(@Valid @RequestBody GoogleRegisterForm googleRegisterForm) {
        ApiMessageDto<CandidateDto> apiMessageDto = new ApiMessageDto<>();

        Account account = accountRepository.findByIdAndStatus(googleRegisterForm.getPlatformUserId(),NextStepConstant.STATUS_PENDING).orElse(null);
        String code = com.nextstep.api.utils.StringUtils.generateRandomString(7);
        if (account == null) {
            throw new BadRequestException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        if (account.getPhone() != null && account.getPlatform() == null) {
            throw new BadRequestException("Account was registered manually, not allowed for Google register", ErrorCode.ACCOUNT_ERROR_ALREADY_EXIST);
        }
        if (account.getResetPwdCode() == null || !account.getResetPwdCode().equals(googleRegisterForm.getCode())) {
            throw new BadRequestException("Invalid verification code", ErrorCode.ACCOUNT_ERROR_CODE_INVALID);
        }
        account.setStatus(NextStepConstant.STATUS_ACTIVE);
        account.setAvatarPath(googleRegisterForm.getAvatar());
        account.setFullName(googleRegisterForm.getFullName());
        account.setResetPwdCode(null);
        account = accountRepository.save(account);

        Candidate candidate = new Candidate();
        candidate.setAccount(account);
        candidate.setCode(code);
        candidate = candidateRepository.save(candidate);

        apiMessageDto.setData(candidateMapper.fromEntityToCandidateDto(candidate));
        apiMessageDto.setMessage("Google register success");
        return apiMessageDto;
    }

    @PutMapping(value = "/change-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAN_U_STATUS')")
    @Transactional
    public ApiMessageDto<String> changeCandidateStatus(@Valid @RequestBody UpdateCandidateStatusForm updateCandidateStatusForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Account account = accountRepository.findByIdAndKind(updateCandidateStatusForm.getId(),NextStepConstant.USER_KIND_CANDIDATE).orElse(null);
        if (account == null) {
            throw new BadRequestException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }

        if (!updateCandidateStatusForm.getStatus().equals(NextStepConstant.STATUS_ACTIVE) && !updateCandidateStatusForm.getStatus().equals(NextStepConstant.STATUS_LOCK)) {
            throw new BadRequestException("Invalid status. Only ACTIVE or LOCK allowed.", ErrorCode.ACCOUNT_ERROR_STATUS_INVALID);
        }

        account.setStatus(updateCandidateStatusForm.getStatus());
        accountRepository.save(account);

        Candidate candidate = candidateRepository.findByAccount(account);
        if (candidate != null) {
            candidate.setStatus(updateCandidateStatusForm.getStatus());
            candidateRepository.save(candidate);
        }

        apiMessageDto.setMessage("Change status successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ApiMessageDto<String> updateCandidateDetail(@RequestBody UpdateCandidateDetailForm updateCandidateDetailForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Long candidateId = getCurrentUser();
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }
        candidateMapper.updateFromUpdateCandidateDetailForm(candidate, updateCandidateDetailForm);
        candidateRepository.save(candidate);

        apiMessageDto.setMessage("Update candidate detail successfully");
        return apiMessageDto;
    }
    @PutMapping(value = "/create-cv", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ApiMessageDto<String> createCv(@RequestBody CreateCvForm createCvForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Long candidateId = getCurrentUser();
        String token = getCurrentToken();
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }
        candidate.setCv(createCvForm.getCv());
        candidate.setCvState(NextStepConstant.CV_EMBEDDING_STATE_PENDING);
        rabbitService.processExtractCvEmbeddingQueue(candidate.getId(),candidate.getCv(),token);
        candidateRepository.save(candidate);

        apiMessageDto.setMessage("Create Cv success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update-favorite", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAN_FAV')")
    @Transactional
    public ApiMessageDto<String> updateFavoritePost(@Valid @RequestBody UpdateFavoritePostForm updateFavoritePostForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Long candidateId = getCurrentUser();
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }

        Post post = postRepository.findById(updateFavoritePostForm.getPostId()).orElse(null);
        if (post == null) {
            throw new BadRequestException("Post not found", ErrorCode.POST_ERROR_NOT_FOUND);
        }
        if (candidate.getFavoritePosts() == null) {
            candidate.setFavoritePosts(new ArrayList<>());
        }

        boolean exists = candidate.getFavoritePosts().stream()
            .anyMatch(p -> p.getId().equals(post.getId()));
        if (updateFavoritePostForm.getState()) {
            if (!exists) {
                candidate.getFavoritePosts().add(post);
            }
        } else {
            if (exists) {
                candidate.getFavoritePosts().removeIf(p -> p.getId().equals(post.getId()));
            } else {
                throw new BadRequestException("Post is not in your favorite list", ErrorCode.POST_ERROR_NOT_FOUND);
            }
        }
        candidateRepository.save(candidate);
        apiMessageDto.setMessage(updateFavoritePostForm.getState() ? "Added to favorites successfully" : "Removed from favorites successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/favorite-posts", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAN_FAV')")
    public ApiMessageDto<ResponseListDto<List<PostDto>>> getFavoritePosts(Pageable pageable) {
        Long candidateId = getCurrentUser();
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            throw new BadRequestException("Candidate not found", ErrorCode.CANDIDATE_ERROR_NOT_FOUND);
        }
        List<Post> favoritePosts = candidate.getFavoritePosts() != null ? candidate.getFavoritePosts() : new ArrayList<>();
        List<PostDto> favoritePostDtos = postMapper.fromEntitiesToPostDtoList(favoritePosts);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), favoritePostDtos.size());
        List<PostDto> pageContent = favoritePostDtos.subList(start, end);
        Page<PostDto> page = new PageImpl<>(pageContent, pageable, favoritePostDtos.size());

        ResponseListDto<List<PostDto>> responseListDto = new ResponseListDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<PostDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get favorite posts successfully");
        return apiMessageDto;
    }

}
