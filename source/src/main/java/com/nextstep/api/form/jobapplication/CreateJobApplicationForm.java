package com.nextstep.api.form.jobapplication;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class CreateJobApplicationForm {

    @NotNull(message = "postId cannot be null")
    @ApiModelProperty(name = "postId", required = true)
    private Long postId;

    @NotBlank(message = "cv cannot be blank")
    @ApiModelProperty(name = "cv", required = true)
    private String cv;

    @ApiModelProperty(name = "candidateInfo")
    private String candidateInfo;

    @ApiModelProperty(name = "coverLetter")
    private String coverLetter;
} 