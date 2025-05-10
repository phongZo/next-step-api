package com.nextstep.api.dto.settings;

import lombok.Data;

@Data
public class SettingsAutoCompleteDto {
    private Long id;
    private String settingKey;
    private String groupName;
}
