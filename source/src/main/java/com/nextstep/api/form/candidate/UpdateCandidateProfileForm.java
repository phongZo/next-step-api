package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UpdateCandidateProfileForm {

    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;

    @ApiModelProperty(name = "password",required = true)
    private String password;

    @ApiModelProperty(name = "oldPassword", required = true)
    private String oldPassword;
    
    @ApiModelProperty(name = "jobTitle",required = true)
    private String jobTitle;
    
    @ApiModelProperty(name = "isAutoApply",required = true)
    private Boolean isAutoApply;
    
    @ApiModelProperty(name = "isJobSearching",required = true)
    private Boolean isJobSearching;
    
    @ApiModelProperty(name = "coverLetter",required = true)
    private String coverLetter;
} 