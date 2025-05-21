package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CandidateSignupForm {

    @NotEmpty(message = "password cant not be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;

    @ApiModelProperty(name = "email",required = false)
    @Email
    private String email;

    @NotEmpty(message = "phone cant not be null")
    @ApiModelProperty(name = "phone", required = true)
    private String phone;

    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName",required = true)
    private String fullName;
    



} 