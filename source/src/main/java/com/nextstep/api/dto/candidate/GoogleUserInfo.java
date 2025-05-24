package com.nextstep.api.dto.candidate;

import lombok.Data;

@Data
public class GoogleUserInfo {
    public String sub;
    public String email;
    public String name;
    public String picture;
}
