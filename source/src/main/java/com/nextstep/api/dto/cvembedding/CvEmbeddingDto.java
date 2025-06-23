package com.nextstep.api.dto.cvembedding;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CvEmbeddingDto {
    @ApiModelProperty(name = "candidateId")
    private Long candidateId;
    @ApiModelProperty(name = "cv")
    private String cv;
}
