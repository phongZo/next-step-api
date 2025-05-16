package com.nextstep.api.dto.candidate;

import com.nextstep.api.dto.account.AccountDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CandidateAdminDto {
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
}
