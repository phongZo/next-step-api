package com.nextstep.api.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoogleVerifyDto {
    public CandidateDto candidate;
    @JsonProperty("isNew")
    public boolean isNew;
}
