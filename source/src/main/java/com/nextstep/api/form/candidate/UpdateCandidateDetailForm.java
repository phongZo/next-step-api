package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateCandidateDetailForm {
    @ApiModelProperty(name = "speciality")
    private String speciality;
    @ApiModelProperty(name = "workArea")
    private String workArea;
    @ApiModelProperty(name = "experience")
    private Integer experience;
    @ApiModelProperty(name = "allowCompanyContact")
    private Boolean allowCompanyContact;
} 