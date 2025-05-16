package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CandidateLoginForm {
    
    @ApiModelProperty(name = "username", required = true)
    @NotEmpty(message = "Username không được để trống")
    private String username;
    
    @ApiModelProperty(name = "password", required = true)
    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;
} 