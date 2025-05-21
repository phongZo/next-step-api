package com.nextstep.api.mapper;

import com.nextstep.api.dto.employee.EmployeeDto;
import com.nextstep.api.form.employee.CreateEmployeeForm;
import com.nextstep.api.form.employee.UpdateEmployeeForm;
import com.nextstep.api.model.Employee;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "code", target = "code")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateEmployeeAdminFormToEntity")
    Employee fromCreateEmployeeFormToEntity(CreateEmployeeForm createEmployeeForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToEmployeeDto")
    EmployeeDto fromEntityToEmployeeDto(Employee employee);

    @IterableMapping(elementTargetType = EmployeeDto.class, qualifiedByName = "fromEntityToEmployeeDto")
    @Named("fromEntitiesToEmployeeDtoList")
    List<EmployeeDto> fromEntitiesToEmployeeDtoList(List<Employee> employees);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "code", target = "code")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateEmployeeAdminForm")
    void updateFromUpdateEmployeeForm(@MappingTarget Employee employee, UpdateEmployeeForm updateEmployeeForm);
}
