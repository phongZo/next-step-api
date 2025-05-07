package com.nextstep.api.dto.service;

import com.nextstep.api.dto.account.AccountDto;
import com.nextstep.api.dto.ABasicAdminDto;
import com.nextstep.api.model.Group;
import lombok.Data;

@Data
public class ServiceDto extends ABasicAdminDto {
    private String serviceName;
    private Group group;
    private String logoPath;
    private String bannerPath;
    private String hotline;
    private String settings;
    private String lang;
    private Integer status;
    private AccountDto accountDto;

    private String tenantId;
}
