package com.nextstep.api.dto.post;

import com.nextstep.api.dto.company.CompanyDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PostAdminDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "experience")
    private Integer experience;
    @ApiModelProperty(name = "level")
    private String level;
    @ApiModelProperty(name = "tag")
    private String tag;
    @ApiModelProperty(name = "expireDate")
    private Date expireDate;
    @ApiModelProperty(name = "type")
    private Integer type;
    @ApiModelProperty(name = "contractType")
    private Integer contractType;
    @ApiModelProperty(name = "company")
    private CompanyDto company;
}
