package com.nextstep.api.form.post;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.validation.PostContractType;
import com.nextstep.api.validation.PostType;
import com.nextstep.api.validation.NationKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class CreatePostForm {
    @NotBlank(message = "name cant not be null")
    @ApiModelProperty(name = "name",required = true)
    private String name;
    @NotBlank(message = "description cant not be null")
    @ApiModelProperty(name = "description", required = true)
    private String description;
    @NotNull(message = "experience can not null")
    @ApiModelProperty(name = "experience",required = true)
    private Integer experience;
    @NotBlank(message = "level cant not be null")
    @ApiModelProperty(name = "level",required = true)
    private String level;
    @NotBlank(message = "tag cant not be null")
    @ApiModelProperty(name = "tag",required = true)
    private String tag;
    @NotNull(message = "expireDate can not be null")
    @Future
    @ApiModelProperty(name = "expireDate",required = true)
    private Date expireDate;
    @NotNull(message = "type can not null")
    @ApiModelProperty(name = "type",required = true)
    @PostType(allowNull = false)
    private Integer type;
    @NotNull(message = "contractType can not null")
    @ApiModelProperty(name = "contractType",required = true)
    @PostContractType(allowNull = false)
    private Integer contractType;
    @NotNull(message = "totalSlot can not be null")
    @Min(value = 1, message = "totalSlot must be at least 1")
    @ApiModelProperty(name = "totalSlot", required = true)
    private Integer totalSlot;
    @NotNull(message = "minSalary can not be null")
    @ApiModelProperty(name = "minSalary", required = true)
    private BigDecimal minSalary;
    @NotNull(message = "maxSalary can not be null")
    @ApiModelProperty(name = "maxSalary", required = true)
    private BigDecimal maxSalary;
    @ApiModelProperty(name = "categoryId")
    private Long categoryId;
    @NotNull(message = "provinceId cannot be null")
    @ApiModelProperty(name = "provinceId",required = true)
    private Long provinceId;
    @NotNull(message = "districtId cannot be null")
    @ApiModelProperty(name = "districtId",required = true)
    private Long districtId;
    @NotNull(message = "wardId cannot be null")
    @ApiModelProperty(name = "wardId",required = true)
    private Long wardId;
}
