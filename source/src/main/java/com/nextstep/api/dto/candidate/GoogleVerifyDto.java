package com.nextstep.api.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
@Data
public class GoogleVerifyDto {
    public Integer platform;
    public Long platformUserId;
    public String code;
    private OAuth2AccessToken oAuth2AccessToken;
}
