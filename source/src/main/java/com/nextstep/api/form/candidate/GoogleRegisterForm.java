package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GoogleRegisterForm {
    @NotNull(message = "platformUserId cant not be null")
    @ApiModelProperty(name = "platformUserId", required = true)
    private Long platformUserId;
    @NotEmpty(message = "code cant not be null")
    @ApiModelProperty(name = "code", required = true)
    private String code;
    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;
    private String avatar;
}
