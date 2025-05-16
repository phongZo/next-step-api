package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CandidateSignupForm {

    @NotEmpty(message = "username cant not be null")
    @ApiModelProperty(name = "username", required = true)
    private String username;

    @NotEmpty(message = "password cant not be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;

    @ApiModelProperty(name = "email")
    @Email
    private String email;

    @ApiModelProperty(name = "phone")
    private String phone;

    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName",required = true)
    private String fullName;
    
    @ApiModelProperty(name = "jobTitle")
    private String jobTitle;
    
    @ApiModelProperty(name = "isAutoApply")
    private Boolean isAutoApply = false;
    
    @ApiModelProperty(name = "isJobSearching")
    private Boolean isJobSearching = false;
    
    @ApiModelProperty(name = "coverLetter")
    private String coverLetter;
} 