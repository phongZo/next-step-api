package com.nextstep.api.dto.post;

import com.nextstep.api.dto.company.CompanyDto;
import com.nextstep.api.dto.nation.NationDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
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
    @ApiModelProperty(name = "totalSlot")
    private Integer totalSlot;
    @ApiModelProperty(name = "minSalary")
    private BigDecimal minSalary;
    @ApiModelProperty(name = "maxSalary")
    private BigDecimal maxSalary;
    @ApiModelProperty(name = "area")
    private NationDto area;
    @ApiModelProperty(name = "province")
    private NationDto province;
    @ApiModelProperty(name = "district")
    private NationDto district;
    @ApiModelProperty(name = "ward")
    private NationDto ward;
}
