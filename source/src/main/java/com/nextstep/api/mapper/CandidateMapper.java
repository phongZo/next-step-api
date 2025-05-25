package com.nextstep.api.mapper;

import com.nextstep.api.dto.candidate.CandidateAdminDto;
import com.nextstep.api.dto.candidate.CandidateDto;
import com.nextstep.api.form.candidate.UpdateCandidateDetailForm;
import com.nextstep.api.form.candidate.UpdateCandidateProfileForm;
import com.nextstep.api.model.Candidate;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "jobTitle", target = "jobTitle")
    @Mapping(source = "isAutoApply", target = "isAutoApply")
    @Mapping(source = "isJobSearching", target = "isJobSearching")
    @Mapping(source = "coverLetter", target = "coverLetter")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDto")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCandidateAdminDto")
    CandidateAdminDto fromEntityToCandidateAdminDto(Candidate candidate);

    @IterableMapping(elementTargetType = CandidateAdminDto.class, qualifiedByName = "fromEntityToCandidateAdminDto")
    @Named("fromEntitiesToCandidateAdminDtoList")
    List<CandidateAdminDto> fromEntitiesToCandidateAdminDtoList(List<Candidate> candidates);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "jobTitle", target = "jobTitle")
    @Mapping(source = "isAutoApply", target = "isAutoApply")
    @Mapping(source = "isJobSearching", target = "isJobSearching")
    @Mapping(source = "coverLetter", target = "coverLetter")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDto")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "experience", target = "experience")
    @Mapping(source = "speciality", target = "speciality")
    @Mapping(source = "workArea", target = "workArea")
    @Mapping(source = "allowCompanyContact", target = "allowCompanyContact")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCandidateDto")
    CandidateDto fromEntityToCandidateDto(Candidate candidate);

    @Mapping(source = "jobTitle", target = "jobTitle")
    @Mapping(source = "isAutoApply", target = "isAutoApply")
    @Mapping(source = "isJobSearching", target = "isJobSearching")
    @Mapping(source = "coverLetter", target = "coverLetter")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateCandidateProfileForm")
    void updateFromUpdateCandidateProfileForm(@MappingTarget Candidate candidate, UpdateCandidateProfileForm updateCandidateProfileForm);


    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "experience", target = "experience")
    @Mapping(source = "speciality", target = "speciality")
    @Mapping(source = "workArea", target = "workArea")
    @Mapping(source = "allowCompanyContact", target = "allowCompanyContact")
    void updateFromUpdateCandidateDetailForm(@MappingTarget Candidate candidate, UpdateCandidateDetailForm form);
}
