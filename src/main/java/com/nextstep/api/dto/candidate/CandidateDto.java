package com.nextstep.api.dto.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDto {
    @ApiModelProperty(name = "id")
    private Long id;
    
    @ApiModelProperty(name = "accountId")
    private Long accountId;
    
    @ApiModelProperty(name = "username")
    private String username;
    
    @ApiModelProperty(name = "email")
    private String email;
    
    @ApiModelProperty(name = "phone")
    private String phone;
    
    @ApiModelProperty(name = "fullName")
    private String fullName;
    
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
    
    @ApiModelProperty(name = "jobTitle")
    private String jobTitle;
    
    @ApiModelProperty(name = "isAutoApply")
    private Boolean isAutoApply;
    
    @ApiModelProperty(name = "isJobSearching")
    private Boolean isJobSearching;
    
    @ApiModelProperty(name = "coverLetter")
    private String coverLetter;
}