package com.nextstep.api.dto.jobapplication;

import com.nextstep.api.dto.candidate.CandidateDto;
import com.nextstep.api.dto.post.PostDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class JobApplicationDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "candidate")
    private CandidateDto candidate;
    @ApiModelProperty(name = "post")
    private PostDto post;
    @ApiModelProperty(name = "cv")
    private String cv;
    @ApiModelProperty(name = "candidateInfo")
    private String candidateInfo;
    @ApiModelProperty(name = "coverLetter")
    private String coverLetter;
    @ApiModelProperty(name = "state")
    private Integer state;
} 