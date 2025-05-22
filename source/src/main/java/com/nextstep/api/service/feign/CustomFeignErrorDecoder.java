package com.nextstep.api.service.feign;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.exception.BadRequestException;
import com.nextstep.api.exception.NotFoundException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

@Component
@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public Exception decode(String methodKey, Response response) {
        Request request = response.request();
        String message = "";
        try (InputStream inputStream = response.body().asInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            message = stringBuilder.toString();
        } catch (Exception e) {
            log.error("Error reading response body", e);
        }

        log.error("Feign error: " + message);
        switch (response.status()) {
            case 401:
                return new RetryableException(401, message, request.httpMethod(), new Date(), request);
            case 403:
                return new RetryableException(403, message, request.httpMethod(), new Date(), request);
            case 400:
                try {
                    ApiMessageDto<String> apiMessageDto = objectMapper.readValue(message, ApiMessageDto.class);
                    String errorCode = apiMessageDto.getErrorCode();
                    return new BadRequestException(apiMessageDto.getMessage(), errorCode);
                } catch (JsonProcessingException e) {
                    log.error("Error parsing response body", e);
                    return new RuntimeException("Invalid response format: " + message);
                }
            case 404:
                try {
                    ApiMessageDto<String> apiMessageDto = objectMapper.readValue(message, ApiMessageDto.class);
                    String errorCode = apiMessageDto.getErrorCode();
                    return new NotFoundException(apiMessageDto.getMessage(), errorCode);
                } catch (JsonProcessingException e) {
                    log.error("Error parsing response body", e);
                    return new RuntimeException("Invalid response format: " + message);
                }
            default:
                return new RuntimeException(message);
        }
    }
}