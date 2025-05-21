package com.nextstep.api.dto.employee;

import com.nextstep.api.dto.ABasicAdminDto;
import com.nextstep.api.dto.account.AccountDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmployeeDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "code")
    private String code;
    @ApiModelProperty(name = "account")
    private AccountDto account;
}
