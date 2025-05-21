package com.nextstep.api.form.company;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class CreateCompanyForm {
    @NotBlank(message = "name cant not be null")
    @ApiModelProperty(name = "name",required = true)
    private String name;
    @NotBlank(message = "description cant not be null")
    @ApiModelProperty(name = "description", required = true)
    private String description;
    @NotBlank(message = "shortDescription cant not be null")
    @ApiModelProperty(name = "shortDescription", required = true)
    private String shortDescription;
    @NotBlank(message = "hotline cant not be null")
    @ApiModelProperty(name = "hotline", required = true)
    private String hotline;
    @NotBlank(message = "logo cant not be null")
    @ApiModelProperty(name = "logo", required = true)
    private String logo;
    @NotBlank(message = "banner cant not be null")
    @ApiModelProperty(name = "banner", required = true)
    private String banner;
}
