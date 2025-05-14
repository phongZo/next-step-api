package com.nextstep.api.form.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nextstep.api.validation.AccountStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateEmployeeForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @ApiModelProperty(name = "email")
    @Email
    private String email;
    @ApiModelProperty(name = "phone")
    private String phone;
    @ApiModelProperty(name = "password", required = false)
    private String password;
    @ApiModelProperty(name = "old password", required = false)
    private String oldPassword;
    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName",example = "Tam Nguyen",required = true)
    private String fullName;
    @ApiModelProperty(name = "avatar path", required = false)
    private String avatarPath;
    @ApiModelProperty(name = "name",required = true)
    private String name;
    @ApiModelProperty(name = "code",required = true)
    private String code;
    @JsonProperty("isManager")
    @ApiModelProperty(name = "isManager",required = true)
    private boolean isManager;
    @ApiModelProperty(name = "status", example = "1", required = true)
    @AccountStatus(allowNull = false)
    private Integer status;
    @ApiModelProperty(name = "companyId",required = true)
    @NotNull(message = "companyId cant not be null")
    private Long companyId;
}
