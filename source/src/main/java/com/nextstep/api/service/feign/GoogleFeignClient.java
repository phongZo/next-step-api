package com.nextstep.api.service.feign;

import com.nextstep.api.dto.candidate.GoogleUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleClient", url = "https://www.googleapis.com")
public interface GoogleFeignClient {
    @GetMapping(value = "/oauth2/v3/userinfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    GoogleUserInfo getUserInfo(@RequestHeader("Authorization") String accessToken);
}
