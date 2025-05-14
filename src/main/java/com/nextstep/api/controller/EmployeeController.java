package com.nextstep.api.controller;


import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.dto.ResponseListDto;
import com.nextstep.api.dto.employee.EmployeeDto;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.form.employee.CreateEmployeeForm;
import com.nextstep.api.form.employee.UpdateEmployeeForm;
import com.nextstep.api.mapper.EmployeeMapper;
import com.nextstep.api.model.Account;
import com.nextstep.api.model.Company;
import com.nextstep.api.model.Employee;
import com.nextstep.api.model.Group;
import com.nextstep.api.model.criteria.EmployeeCriteria;
import com.nextstep.api.repository.AccountRepository;
import com.nextstep.api.repository.CompanyRepository;
import com.nextstep.api.repository.EmployeeRepository;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/employee")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class EmployeeController extends ABasicController {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    CompanyRepository companyRepository;


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EMP_L')")
    public ApiMessageDto<ResponseListDto<List<EmployeeDto>>> getEmployeeList(
            EmployeeCriteria employeeCriteria,
            Pageable pageable
    ) {
        Specification<Employee> specification = employeeCriteria.getSpecification();
        Page<Employee> page = employeeRepository.findAll(specification, pageable);

        ResponseListDto<List<EmployeeDto>> responseListDto = new ResponseListDto<>(
                employeeMapper.fromEntitiesToEmployeeDtoList(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<EmployeeDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get employee list successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EMP_V')")
    public ApiMessageDto<EmployeeDto> getEmployee(@PathVariable Long id) {
        ApiMessageDto<EmployeeDto> apiMessageDto = new ApiMessageDto<>();
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found",  ErrorCode.EMPLOYEE_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(employeeMapper.fromEntityToEmployeeDto(employee));
        apiMessageDto.setMessage("Get employee successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EMP_C')")
    @Transactional
    public ApiMessageDto<String> createEmployee(
            @Valid @RequestBody CreateEmployeeForm createEmployeeForm,
            BindingResult bindingResult
    ){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(accountRepository.existsByPhone(createEmployeeForm.getPhone())){
            throw new BadRequestException("Phone number already in use", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
        }
        Group group = groupRepository.findFirstByKind(NextStepConstant.GROUP_KIND_EMPLOYEE);
        if(group == null){
            throw new BadRequestException("Group not found",  ErrorCode.GROUP_ERROR_NOT_FOUND);
        }
        Company company = companyRepository.findById(createEmployeeForm.getCompanyId()).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found", ErrorCode.COMPANY_ERROR_NOT_FOUND);
        }
        Account account = new Account();
        account.setKind(NextStepConstant.USER_KIND_EMPLOYEE);
        account.setPassword(passwordEncoder.encode(createEmployeeForm.getPassword()));
        account.setPhone(createEmployeeForm.getPhone());
        account.setEmail(createEmployeeForm.getEmail());
        account.setFullName(createEmployeeForm.getFullName());
        account.setAvatarPath(createEmployeeForm.getAvatarPath());
        account.setGroup(group);
        account.setStatus(NextStepConstant.STATUS_ACTIVE);
        Account savedAccount = accountRepository.save(account);

        Employee employee = employeeMapper.fromCreateEmployeeFormToEntity(createEmployeeForm);
        employee.setAccount(savedAccount);
        employee.setCompany(company);
        employeeRepository.save(employee);
        apiMessageDto.setMessage("Create employee successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EMP_U')")
    @Transactional
    public ApiMessageDto<String> updateEmployee(
            @Valid @RequestBody UpdateEmployeeForm updateEmployeeForm,
            BindingResult bindingResult
    ){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Employee employee = employeeRepository.findById(updateEmployeeForm.getId()).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found",  ErrorCode.EMPLOYEE_ERROR_NOT_FOUND);
        }
        Account account = employee.getAccount();
        if (!account.getPhone().equals(updateEmployeeForm.getPhone())) {
            if (accountRepository.existsByPhone(updateEmployeeForm.getPhone())) {
                throw new BadRequestException("Phone number already in use", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
            }
            account.setPhone(updateEmployeeForm.getPhone());
        }
        if (StringUtils.isNoneBlank(updateEmployeeForm.getPassword())
                || StringUtils.isNoneBlank(updateEmployeeForm.getOldPassword())) {

            if (!StringUtils.isNoneBlank(updateEmployeeForm.getOldPassword())) {
                throw new BadRequestException("Old password can not be empty", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
            }
            if (!StringUtils.isNoneBlank(updateEmployeeForm.getPassword())) {
                throw new BadRequestException("New password can not be empty", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
            }
            if (!passwordEncoder.matches(updateEmployeeForm.getOldPassword(), account.getPassword())) {
                throw new BadRequestException("Old password is not correct", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
            }
            if (!passwordEncoder.matches(updateEmployeeForm.getPassword(), account.getPassword())) {
                account.setPassword(passwordEncoder.encode(updateEmployeeForm.getPassword()));
            }
        }
        if (!employee.getCompany().getId().equals(updateEmployeeForm.getCompanyId())) {
            Company company = companyRepository.findById(updateEmployeeForm.getCompanyId()).orElse(null);
            if (company == null) {
                throw new BadRequestException("Company not found", ErrorCode.COMPANY_ERROR_NOT_FOUND);
            }
            employee.setCompany(company);
        }
        account.setPhone(updateEmployeeForm.getPhone());
        account.setEmail(updateEmployeeForm.getEmail());
        account.setFullName(updateEmployeeForm.getFullName());
        account.setStatus(updateEmployeeForm.getStatus());
        account.setAvatarPath(updateEmployeeForm.getAvatarPath());
        accountRepository.save(account);
        employeeMapper.updateFromUpdateEmployeeForm(employee, updateEmployeeForm);

        employee.setManager(updateEmployeeForm.isManager());
        employeeRepository.save(employee);
        apiMessageDto.setMessage("Update employee successfully");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EMP_D')")
    @Transactional
    public ApiMessageDto<String> deleteEmployee(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            throw new BadRequestException("Employee not found",  ErrorCode.EMPLOYEE_ERROR_NOT_FOUND);
        }
        // Delete EMPLOYEE
        employeeRepository.deleteById(id);
        // Delete ACCOUNT
        accountRepository.deleteById(id);
        apiMessageDto.setMessage("Delete employee successfully");
        return apiMessageDto;
    }

}
