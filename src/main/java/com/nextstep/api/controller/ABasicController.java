package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.jwt.NextStepJwt;
import com.nextstep.api.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Objects;

public class ABasicController {
    @Autowired
    private UserServiceImpl userService;

    public long getCurrentUser(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        return nextStepJwt.getAccountId();
    }

    public long getTokenId(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        return nextStepJwt.getTokenId();
    }

    public NextStepJwt getSessionFromToken(){
        return userService.getAddInfoFromToken();
    }

    public boolean isSuperAdmin(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        if(nextStepJwt !=null){
            return nextStepJwt.getIsSuperAdmin();
        }
        return false;
    }

    public boolean isShop(){
        NextStepJwt nextStepJwt = userService.getAddInfoFromToken();
        if(nextStepJwt !=null){
            return Objects.equals(nextStepJwt.getUserKind(), NextStepConstant.USER_KIND_MANAGER);
        }
        return false;
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                return oauthDetails.getTokenValue();
            }
        }
        return null;
    }
}
