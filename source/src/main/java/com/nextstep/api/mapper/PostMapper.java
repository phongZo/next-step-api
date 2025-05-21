package com.nextstep.api.mapper;

import com.nextstep.api.dto.company.CompanyDto;
import com.nextstep.api.dto.employee.EmployeeDto;
import com.nextstep.api.dto.post.PostAdminDto;
import com.nextstep.api.dto.post.PostDto;
import com.nextstep.api.form.employee.CreateEmployeeForm;
import com.nextstep.api.form.employee.UpdateEmployeeForm;
import com.nextstep.api.form.post.CreatePostForm;
import com.nextstep.api.form.post.UpdatePostForm;
import com.nextstep.api.model.Company;
import com.nextstep.api.model.Employee;
import com.nextstep.api.model.Post;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "expireDate", target = "expireDate")
    @Mapping(source = "tag", target = "tag")
    @Mapping(source = "level", target = "level")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "contractType", target = "contractType")
    @Mapping(source = "experience", target = "experience")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreatePostFormToEntity")
    Post fromCreatePostFormToEntity(CreatePostForm createPostForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "contractType", target = "contractType")
    @Mapping(source = "expireDate", target = "expireDate")
    @Mapping(source = "tag", target = "tag")
    @Mapping(source = "level", target = "level")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "experience", target = "experience")
    @Mapping(source = "company", target = "company", qualifiedByName = "fromEntityToCompanyDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPostAdminDto")
    PostAdminDto fromEntityToPostAdminDto(Post post);

    @IterableMapping(elementTargetType = PostAdminDto.class, qualifiedByName = "fromEntityToPostAdminDto")
    @Named("fromEntitiesToPostAdminDtoList")
    List<PostAdminDto> fromEntitiesToPostAdminDtoList(List<Post> posts);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "contractType", target = "contractType")
    @Mapping(source = "expireDate", target = "expireDate")
    @Mapping(source = "tag", target = "tag")
    @Mapping(source = "level", target = "level")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "experience", target = "experience")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdatePostForm")
    void updateFromUpdatePostForm(@MappingTarget Post post, UpdatePostForm updatePostForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "contractType", target = "contractType")
    @Mapping(source = "expireDate", target = "expireDate")
    @Mapping(source = "tag", target = "tag")
    @Mapping(source = "level", target = "level")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "experience", target = "experience")
    @Mapping(source = "company", target = "company", qualifiedByName = "fromEntityToCompanyDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPostDto")
    PostDto fromEntityToPostDto(Post post);

    @IterableMapping(elementTargetType = PostDto.class, qualifiedByName = "fromEntityToPostDto")
    @Named("fromEntitiesToPostAdminDtoList")
    List<PostDto> fromEntitiesToPostDtoList(List<Post> posts);

}
