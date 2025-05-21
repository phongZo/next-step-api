package com.nextstep.api.dto.user;

import com.nextstep.api.dto.account.AccountAutoCompleteDto;
import lombok.Data;

@Data
public class UserAutoCompleteDto {

    private Long id;
    private AccountAutoCompleteDto accountAutoCompleteDto;
}
