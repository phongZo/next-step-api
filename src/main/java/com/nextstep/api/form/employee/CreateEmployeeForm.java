package com.nextstep.api.form.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CreateEmployeeForm {
    @ApiModelProperty(name = "email")
    @Email
    private String email;
    @ApiModelProperty(name = "phone")
    private String phone;
    @NotEmpty(message = "password cant not be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;
    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName",example = "Tam Nguyen",required = true)
    private String fullName;
    private String avatarPath;
    @ApiModelProperty(name = "name",required = true)
    private String name;
    @ApiModelProperty(name = "code",required = true)
    private String code;
    @ApiModelProperty(name = "isManager",required = true)
    private boolean isManager;
}
