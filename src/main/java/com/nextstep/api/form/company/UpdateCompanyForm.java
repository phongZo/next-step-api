package com.nextstep.api.form.company;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateCompanyForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
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
