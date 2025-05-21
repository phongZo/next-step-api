package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.jwt.NextStepJwt;
import com.nextstep.api.model.Employee;
import com.nextstep.api.repository.EmployeeRepository;
import com.nextstep.api.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Objects;

public class ABasicController {
    @Autowired
    private UserServiceImpl userService;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    public long getCurrentUser(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        return nextStepJwt.getAccountId();
    }

    public long getTokenId(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        return nextStepJwt.getTokenId();
    }

    public NextStepJwt getSessionFromToken(){
        return userService.getAddInfoFromToken();
    }

    public boolean isSuperAdmin(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        if(nextStepJwt !=null){
            return nextStepJwt.getIsSuperAdmin();
        }
        return false;
    }

    public boolean isShop(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        if(nextStepJwt !=null){
            return Objects.equals(nextStepJwt.getUserKind(), NextStepConstant.USER_KIND_MANAGER);
        }
        return false;
    }
    
    public boolean isEmployee(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        if(nextStepJwt !=null){
            return Objects.equals(nextStepJwt.getUserKind(), NextStepConstant.USER_KIND_EMPLOYEE);
        }
        return false;
    }
    
    public Long getCurrentEmployeeCompanyId(){
        if (!isEmployee()) {
            throw new BadRequestException("User is not an employee", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        Long accountId = getCurrentUser();
        Employee employee = employeeRepository.findById(accountId).orElse(null);
        if (employee == null || employee.getCompany() == null) {
            throw new BadRequestException("Employee or company not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND);
        }
        return employee.getCompany().getId();
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                return oauthDetails.getTokenValue();
            }
        }
        return null;
    }
}
