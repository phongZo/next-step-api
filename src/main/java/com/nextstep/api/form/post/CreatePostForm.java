package com.nextstep.api.form.post;

import com.nextstep.api.validation.PostContractType;
import com.nextstep.api.validation.PostType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "companyId cannot be null ")
    @ApiModelProperty(name = "companyId", required = true)
    private Long companyId;

}
