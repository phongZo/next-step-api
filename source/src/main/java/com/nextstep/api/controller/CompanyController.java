package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.dto.ResponseListDto;
import com.nextstep.api.dto.company.CompanyDto;
import com.nextstep.api.dto.employee.EmployeeDto;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.form.company.CreateCompanyForm;
import com.nextstep.api.form.company.UpdateCompanyForm;
import com.nextstep.api.form.employee.CreateEmployeeForm;
import com.nextstep.api.form.employee.UpdateEmployeeForm;
import com.nextstep.api.mapper.CompanyMapper;
import com.nextstep.api.mapper.EmployeeMapper;
import com.nextstep.api.model.Account;
import com.nextstep.api.model.Company;
import com.nextstep.api.model.Employee;
import com.nextstep.api.model.Group;
import com.nextstep.api.model.criteria.CompanyCriteria;
import com.nextstep.api.model.criteria.EmployeeCriteria;
import com.nextstep.api.repository.CompanyRepository;
import com.nextstep.api.repository.EmployeeRepository;
import com.nextstep.api.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;

@RestController
@RequestMapping("/v1/company")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CompanyController extends ABasicController{

    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    CompanyMapper companyMapper;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    PostRepository postRepository;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_L')")
    public ApiMessageDto<ResponseListDto<List<CompanyDto>>> getCompanyList(
            CompanyCriteria companyCriteria,
            Pageable pageable
    ) {
        Specification<Company> specification = companyCriteria.getSpecification();
        Page<Company> page = companyRepository.findAll(specification, pageable);

        ResponseListDto<List<CompanyDto>> responseListDto = new ResponseListDto<>(
                companyMapper.fromEntitiesToCompanyDtoList(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<CompanyDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get company list successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_V')")
    public ApiMessageDto<CompanyDto> getCompany(@PathVariable Long id) {
        ApiMessageDto<CompanyDto> apiMessageDto = new ApiMessageDto<>();
        Company company = companyRepository.findById(id).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found",  ErrorCode.COMPANY_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(companyMapper.fromEntityToCompanyDto(company));
        apiMessageDto.setMessage("Get company successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_C')")
    @Transactional
    public ApiMessageDto<String> createCompany(
            @Valid @RequestBody CreateCompanyForm createCompanyForm,
            BindingResult bindingResult
    ){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if (companyRepository.existsByNameAndStatus(createCompanyForm.getName(), NextStepConstant.STATUS_ACTIVE)) {
            throw new BadRequestException("Company name exist", ErrorCode.COMPANY_ERROR_EXIST);
        }
        if (companyRepository.existsByHotlineAndStatus(createCompanyForm.getHotline(), NextStepConstant.STATUS_ACTIVE)) {
            throw new BadRequestException("Company hotline exist", ErrorCode.COMPANY_ERROR_EXIST);
        }
        Company company =companyMapper.fromCreateCompanyFormToEntity(createCompanyForm);
        companyRepository.save(company);
        apiMessageDto.setMessage("Create company successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_U')")
    @Transactional
    public ApiMessageDto<String> updateCompany(
            @Valid @RequestBody UpdateCompanyForm updateCompanyForm,
            BindingResult bindingResult
    ){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Company company = companyRepository.findById(updateCompanyForm.getId()).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found",  ErrorCode.COMPANY_ERROR_NOT_FOUND);
        }
        if (!company.getName().equals(updateCompanyForm.getName())) {
            if (companyRepository.existsByNameAndStatus(updateCompanyForm.getName(),NextStepConstant.STATUS_ACTIVE)) {
                throw new BadRequestException("Company name exist", ErrorCode.COMPANY_ERROR_EXIST);
            }
            company.setName(updateCompanyForm.getName());
        }
        if (!company.getHotline().equals(updateCompanyForm.getHotline())) {
            if (companyRepository.existsByHotlineAndStatus(updateCompanyForm.getHotline(),NextStepConstant.STATUS_ACTIVE)) {
                throw new BadRequestException("Company hotline exist", ErrorCode.COMPANY_ERROR_EXIST);
            }
            company.setHotline(updateCompanyForm.getHotline());
        }
        companyMapper.updateFromUpdateCompanyForm(company, updateCompanyForm);
        companyRepository.save(company);
        apiMessageDto.setMessage("Update company successfully");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_D')")
    @Transactional
    public ApiMessageDto<String> deleteCompany(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Company company = companyRepository.findById(id).orElse(null);
        if (company == null) {
            throw new BadRequestException("Company not found",  ErrorCode.COMPANY_ERROR_NOT_FOUND);
        }
        if (employeeRepository.existsByCompanyId(id)) {
            throw new BadRequestException("Cannot delete company that has employees", ErrorCode.COMPANY_ERROR_HAS_EMPLOYEES);
        }
        postRepository.deleteAllByCompanyId(id);
        companyRepository.deleteById(id);
        apiMessageDto.setMessage("Delete company successfully");
        return apiMessageDto;
    }
}
