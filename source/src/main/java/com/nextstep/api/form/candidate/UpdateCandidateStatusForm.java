package com.nextstep.api.form.candidate;

import com.nextstep.api.validation.AccountStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateCandidateStatusForm {
    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "Status is required")
    private Integer status;
}
