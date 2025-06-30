package com.nextstep.api.form.jobapplication;

import com.nextstep.api.validation.JobApplicationState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class ChangeJobApplicationStateForm {
    @ApiModelProperty(name = "id", required = true)
    @NotNull(message = "Job application ID is required")
    private Long id;

    @ApiModelProperty(name = "state", required = true)
    @NotNull(message = "State is required")
    @JobApplicationState
    private Integer state;
} 