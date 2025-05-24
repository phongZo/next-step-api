package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GoogleVerifyForm {
    @NotEmpty(message = "accessToken cant not be null")
    @ApiModelProperty(name = "accessToken", required = true)
    private String accessToken;
}
