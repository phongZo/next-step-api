package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateCvForm {
    @NotEmpty(message = "cv cant not be null")
    @ApiModelProperty(name = "cv", required = true)
    private String cv;
}
