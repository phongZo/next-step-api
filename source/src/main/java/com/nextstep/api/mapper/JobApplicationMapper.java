package com.nextstep.api.mapper;

import com.nextstep.api.dto.nation.NationAdminDto;
import com.nextstep.api.form.jobapplication.CreateJobApplicationForm;
import com.nextstep.api.model.JobApplication;
import com.nextstep.api.dto.jobapplication.JobApplicationDto;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    @Mapping(source = "cv", target = "cv")
    @Mapping(source = "candidateInfo", target = "candidateInfo")
    @Mapping(source = "coverLetter", target = "coverLetter")
    @BeanMapping(ignoreByDefault = true)
    JobApplication fromCreateFormJobApplicationToEntity(CreateJobApplicationForm form);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "candidate", target = "candidate")
    @Mapping(source = "post", target = "post")
    @Mapping(source = "cv", target = "cv")
    @Mapping(source = "candidateInfo", target = "candidateInfo")
    @Mapping(source = "coverLetter", target = "coverLetter")
    @Mapping(source = "state", target = "state")
    @Named("fromEntityToJobApplicationDto")
    JobApplicationDto fromEntityToJobApplicationDto(JobApplication jobApplication);

    @IterableMapping(elementTargetType = JobApplicationDto.class, qualifiedByName = "fromEntityToJobApplicationDto")
    List<JobApplicationDto> fromEntitiesToJobApplicationDtoList(List<JobApplication> jobApplications);

} 