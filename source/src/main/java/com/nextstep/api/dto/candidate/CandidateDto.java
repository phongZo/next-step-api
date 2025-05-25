package com.nextstep.api.dto.candidate;

import com.nextstep.api.dto.ABasicAdminDto;
import com.nextstep.api.dto.account.AccountDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
public class CandidateDto extends ABasicAdminDto {
    @ApiModelProperty(name = "id")
    private Long id;
    
    @ApiModelProperty(name = "jobTitle")
    private String jobTitle;
    
    @ApiModelProperty(name = "isAutoApply")
    private Boolean isAutoApply;
    
    @ApiModelProperty(name = "isJobSearching")
    private Boolean isJobSearching;
    
    @ApiModelProperty(name = "coverLetter")
    private String coverLetter;

    @ApiModelProperty(name = "account")
    private AccountDto account;

    @ApiModelProperty(name = "code")
    private String code;

    @ApiModelProperty(name = "experience")
    private Integer experience;

    @ApiModelProperty(name = "speciality")
    private String speciality;

    @ApiModelProperty(name = "workArea")
    private String workArea;

    @ApiModelProperty(name = "allowCompanyContact")
    private Boolean allowCompanyContact;
}