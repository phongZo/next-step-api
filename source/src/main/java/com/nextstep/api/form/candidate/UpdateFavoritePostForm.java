package com.nextstep.api.form.candidate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateFavoritePostForm {
    @NotNull(message = "postId cannot be null")
    @ApiModelProperty(name = "postId", required = true)
    private Long postId;

    @NotNull(message = "state cannot be null")
    @ApiModelProperty(name = "state", required = true, example = "true for like, false for unlike")
    private Boolean state;
} 