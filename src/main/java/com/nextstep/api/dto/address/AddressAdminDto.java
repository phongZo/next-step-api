package com.nextstep.api.dto.address;

import com.nextstep.api.dto.ABasicAdminDto;
import com.nextstep.api.dto.nation.NationAdminDto;
import com.nextstep.api.dto.user.UserDto;
import lombok.Data;

@Data
public class AddressAdminDto extends ABasicAdminDto {
    private String address;
    private NationAdminDto wardInfo;
    private NationAdminDto districtInfo;
    private NationAdminDto provinceInfo;
    private String name;
    private String phone;
    private UserDto userInfo;
}
