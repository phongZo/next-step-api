package com.nextstep.api.mapper;


import com.nextstep.api.dto.company.CompanyDto;
import com.nextstep.api.dto.employee.EmployeeDto;
import com.nextstep.api.form.company.CreateCompanyForm;
import com.nextstep.api.form.company.UpdateCompanyForm;
import com.nextstep.api.form.employee.UpdateEmployeeForm;
import com.nextstep.api.model.Company;
import com.nextstep.api.model.Employee;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "hotline", target = "hotline")
    @Mapping(source = "logo", target = "logo")
    @Mapping(source = "banner", target = "banner")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateCompanyFormToEntity")
    Company fromCreateCompanyFormToEntity(CreateCompanyForm createCompanyForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "hotline", target = "hotline")
    @Mapping(source = "logo", target = "logo")
    @Mapping(source = "banner", target = "banner")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCompanyDto")
    CompanyDto fromEntityToCompanyDto(Company company);

    @IterableMapping(elementTargetType = EmployeeDto.class, qualifiedByName = "fromEntityToCompanyDto")
    @Named("fromEntitiesToCompanyDtoList")
    List<CompanyDto> fromEntitiesToCompanyDtoList(List<Company> employees);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "hotline", target = "hotline")
    @Mapping(source = "logo", target = "logo")
    @Mapping(source = "banner", target = "banner")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateCompanyForm")
    void updateFromUpdateCompanyForm(@MappingTarget Company company, UpdateCompanyForm updateCompanyForm);
}
