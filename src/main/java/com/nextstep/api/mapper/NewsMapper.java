package com.nextstep.api.mapper;

import com.nextstep.api.dto.news.NewsAdminDto;
import com.nextstep.api.dto.news.NewsAutoCompleteDto;
import com.nextstep.api.form.news.CreateNewsForm;
import com.nextstep.api.form.news.UpdateNewsForm;
import com.nextstep.api.model.News;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = CategoryMapper.class)

public interface NewsMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "pinTop", target = "pinTop")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNewsAdminDto")
    NewsAdminDto fromEntityToNewsAdminDto(News news);

    @IterableMapping(elementTargetType = NewsAdminDto.class, qualifiedByName = "fromEntityToNewsAdminDto")
    List<NewsAdminDto> fromEntityToNewsAdminDtoList(List<News> news);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "pinTop", target = "pinTop")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNewsAutoCompleteDto")
    NewsAutoCompleteDto fromEntityToNewsAutoCompleteDto(News news);

    @IterableMapping(elementTargetType = NewsAutoCompleteDto.class, qualifiedByName = "fromEntityToNewsAutoCompleteDto")
    List<NewsAutoCompleteDto> fromEntityToNewsAutoCompleteDtoList(List<News> courses);

    @Mapping(source = "description", target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateNewsFormToEntity")
    News fromCreateNewsFormToEntity(CreateNewsForm createNewsForm);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "pinTop", target = "pinTop")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateCourseNotStartedFromForm")
    void updateNewsFromUpdateNewsForm(UpdateNewsForm updateNewsForm, @MappingTarget News news);
}