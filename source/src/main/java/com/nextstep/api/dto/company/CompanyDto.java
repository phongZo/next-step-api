package com.nextstep.api.dto.company;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "shortDescription")
    private String shortDescription;
    @ApiModelProperty(name = "hotline")
    private String hotline;
    @ApiModelProperty(name = "logo")
    private String logo;
    @ApiModelProperty(name = "banner")
    private String banner;
}
