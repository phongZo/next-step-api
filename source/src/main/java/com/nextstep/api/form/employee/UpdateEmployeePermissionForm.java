package com.nextstep.api.form.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel
public class UpdateEmployeePermissionForm {
    @NotNull(message = "employeeId cannot be null")
    @ApiModelProperty(name = "employeeId", required = true)
    private Long employeeId;

    @NotNull(message = "permissionIds cannot be null")
    @ApiModelProperty(name = "permissionIds", required = true)
    private List<Long> permissionIds;
} 