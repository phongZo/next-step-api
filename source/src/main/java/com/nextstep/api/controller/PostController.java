package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.dto.ResponseListDto;
import com.nextstep.api.dto.post.PostAdminDto;
import com.nextstep.api.dto.post.PostDto;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.form.post.CreatePostForm;
import com.nextstep.api.form.post.UpdatePostForm;
import com.nextstep.api.mapper.PostMapper;
import com.nextstep.api.model.*;
import com.nextstep.api.model.criteria.PostCriteria;
import com.nextstep.api.repository.*;
import com.nextstep.api.service.rabbit.RabbitService;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/post")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class PostController extends ABasicController{

    @Autowired
    PostRepository postRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    NationRepository nationRepository;
    @Autowired
    PostMapper postMapper;
    @Autowired
    RabbitService rabbitService;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('POST_L')")
    public ApiMessageDto<ResponseListDto<List<PostAdminDto>>> getPostList(
            PostCriteria postCriteria,
            Pageable pageable
    ) {
        Specification<Post> specification = postCriteria.getSpecification();
        Page<Post> page = postRepository.findAll(specification, pageable);

        ResponseListDto<List<PostAdminDto>> responseListDto = new ResponseListDto<>(
                postMapper.fromEntitiesToPostAdminDtoList(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<PostAdminDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get post list successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('POST_V')")
    public ApiMessageDto<PostAdminDto> getPost(@PathVariable Long id) {
        ApiMessageDto<PostAdminDto> apiMessageDto = new ApiMessageDto<>();
        Post post = postRepository.findById(id).orElse(null);
        if(post == null){
            throw new BadRequestException("Post not found", ErrorCode.POST_ERROR_NOT_FOUND);
        }
        
        if (isEmployee()) {
            Long currentCompanyId = getCurrentEmployeeCompanyId();
            if (!post.getCompany().getId().equals(currentCompanyId)) {
                throw new BadRequestException("You can only view posts of your company", ErrorCode.POST_ERROR_NOT_FOUND);
            }
        }
        
        apiMessageDto.setData(postMapper.fromEntityToPostAdminDto(post));
        apiMessageDto.setMessage("Get post successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('POST_C')")
    @Transactional
    public ApiMessageDto<String> createPost(
            @Valid @RequestBody CreatePostForm createPostForm,
            BindingResult bindingResult
    ){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        if (createPostForm.getMinSalary().compareTo(createPostForm.getMaxSalary()) > 0) {
            throw new BadRequestException("minSalary must be less than or equal to maxSalary",ErrorCode.POST_ERROR_INVALID_SALARY);
        }
        Long companyId = getCurrentEmployeeCompanyId();
        Company company = companyRepository.findById(companyId).orElse(null);
        
        if (company == null) {
            throw new BadRequestException("Company not found", ErrorCode.COMPANY_ERROR_NOT_FOUND);
        }

        Category job = null;
        if (createPostForm.getCategoryId() != null) {
            job = categoryRepository.findById(createPostForm.getCategoryId()).orElse(null);
            if (job == null) {
                throw new BadRequestException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            }
        }
        Post post = postMapper.fromCreatePostFormToEntity(createPostForm);
        if (createPostForm.getProvinceId() != null) {
            Nation province = nationRepository.findById(createPostForm.getProvinceId()).orElse(null);
            if (province == null || !Objects.equals(province.getKind(), NextStepConstant.NATION_KIND_PROVINCE)) {
                throw new BadRequestException("provinceId must be a valid province", ErrorCode.NATION_ERROR_NOT_FOUND);
            }
            post.setProvince(province);
        }
        if (createPostForm.getDistrictId() != null) {
            Nation district = nationRepository.findById(createPostForm.getDistrictId()).orElse(null);
            if (district == null || !Objects.equals(district.getKind(), NextStepConstant.NATION_KIND_DISTRICT)) {
                throw new BadRequestException("districtId must be a valid district", ErrorCode.NATION_ERROR_NOT_FOUND);
            }
            post.setDistrict(district);
        }
        if (createPostForm.getWardId() != null) {
            Nation ward = nationRepository.findById(createPostForm.getWardId()).orElse(null);
            if (ward == null || !Objects.equals(ward.getKind(), NextStepConstant.NATION_KIND_WARD)) {
                throw new BadRequestException("wardId must be a valid ward", ErrorCode.NATION_ERROR_NOT_FOUND);
            }
            post.setWard(ward);
        }
        String token = getCurrentToken();
        

        post.setState(NextStepConstant.POST_EMBEDDING_STATE_PENDING);
        post.setCompany(company);
        post.setCategory(job);
        postRepository.save(post);
        rabbitService.processCvEmbeddingQueue(post.getId(), post.getDescription(), token);
        apiMessageDto.setMessage("Create post successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('POST_U')")
    @Transactional
    public ApiMessageDto<String> updatePost(
            @Valid @RequestBody UpdatePostForm updatePostForm,
            BindingResult bindingResult
    ){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        
        if (!isEmployee()) {
            throw new BadRequestException("Only employees can update posts", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        
        Post post = postRepository.findById(updatePostForm.getId()).orElse(null);
        if(post == null){
            throw new BadRequestException("Post not found", ErrorCode.POST_ERROR_NOT_FOUND);
        }
        if (updatePostForm.getMinSalary().compareTo(updatePostForm.getMaxSalary()) > 0) {
            throw new BadRequestException("minSalary must be less than or equal to maxSalary",ErrorCode.POST_ERROR_INVALID_SALARY);
        }
        
        Long currentCompanyId = getCurrentEmployeeCompanyId();
        if (!post.getCompany().getId().equals(currentCompanyId)) {
            throw new BadRequestException("You can only update posts of your company", ErrorCode.POST_ERROR_NOT_FOUND);
        }

        
        if (updatePostForm.getCategoryId() != null) {
            Category job = categoryRepository.findById(updatePostForm.getCategoryId()).orElse(null);
            if (job == null) {
                throw new BadRequestException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            }
            post.setCategory(job);
        }
        
        if (updatePostForm.getProvinceId() != null) {
            Nation province = nationRepository.findById(updatePostForm.getProvinceId()).orElse(null);
            if (province == null || !Objects.equals(province.getKind(), NextStepConstant.NATION_KIND_PROVINCE)) {
                throw new BadRequestException("provinceId must be a valid province", ErrorCode.NATION_ERROR_NOT_FOUND);
            }
            post.setProvince(province);
        }
        if (updatePostForm.getDistrictId() != null) {
            Nation district = nationRepository.findById(updatePostForm.getDistrictId()).orElse(null);
            if (district == null || !Objects.equals(district.getKind(), NextStepConstant.NATION_KIND_DISTRICT)) {
                throw new BadRequestException("districtId must be a valid district", ErrorCode.NATION_ERROR_NOT_FOUND);
            }
            post.setDistrict(district);
        }
        if (updatePostForm.getWardId() != null) {
            Nation ward = nationRepository.findById(updatePostForm.getWardId()).orElse(null);
            if (ward == null || !Objects.equals(ward.getKind(), NextStepConstant.NATION_KIND_WARD)) {
                throw new BadRequestException("wardId must be a valid ward", ErrorCode.NATION_ERROR_NOT_FOUND);
            }
            post.setWard(ward);
        }
        
        postMapper.updateFromUpdatePostForm(post, updatePostForm);
        postRepository.save(post);
        apiMessageDto.setMessage("Update post successfully");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('POST_D')")
    @Transactional
    public ApiMessageDto<String> deletePost(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        
        if (!isEmployee()) {
            throw new BadRequestException("Only employees can delete posts", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        
        Post post = postRepository.findById(id).orElse(null);
        if(post == null){
            throw new BadRequestException("Post not found", ErrorCode.POST_ERROR_NOT_FOUND);
        }
        
        Long currentCompanyId = getCurrentEmployeeCompanyId();
        if (!post.getCompany().getId().equals(currentCompanyId)) {
            throw new BadRequestException("You can only delete posts of your company", ErrorCode.POST_ERROR_NOT_FOUND);
        }
        
        postRepository.delete(post);
        apiMessageDto.setMessage("Delete Post successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<PostDto> getForClientPost(@PathVariable Long id) {
        ApiMessageDto<PostDto> apiMessageDto = new ApiMessageDto<>();
        Post post = postRepository.findById(id).orElse(null);
        if(post == null){
            throw new BadRequestException("Post not found", ErrorCode.POST_ERROR_NOT_FOUND);
        }

        if (isEmployee()) {
            Long currentCompanyId = getCurrentEmployeeCompanyId();
            if (!post.getCompany().getId().equals(currentCompanyId)) {
                throw new BadRequestException("You can only view posts of your company", ErrorCode.POST_ERROR_NOT_FOUND);
            }
        }

        apiMessageDto.setData(postMapper.fromEntityToPostDto(post));
        apiMessageDto.setMessage("Get post successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<PostDto>>> getPostListForClient(
            PostCriteria postCriteria,
            Pageable pageable
    ) {
        Specification<Post> specification = postCriteria.getSpecification();
        Page<Post> page = postRepository.findAll(specification, pageable);
        List<PostDto> postDtos = postMapper.fromEntitiesToPostDtoList(page.getContent());
        String token = getCurrentToken();
        if (token != null && !token.isEmpty()) {
            Long candidateId = getCurrentUser();
            Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
            if (candidate != null && candidate.getFavoritePosts() != null) {
                Set<Long> favoritePostIds = candidate.getFavoritePosts().stream()
                        .map(Post::getId)
                        .collect(Collectors.toSet());
                postDtos.forEach(dto -> dto.setIsFavorite(favoritePostIds.contains(dto.getId())));
            }
        }
        ResponseListDto<List<PostDto>> responseListDto = new ResponseListDto<>(
                postDtos,
                page.getTotalElements(),
                page.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<PostDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get post list successfully");
        return apiMessageDto;
    }
}
