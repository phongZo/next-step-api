package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.form.candidate.CandidateSignupForm;
import com.nextstep.api.form.user.SignUpUserForm;
import com.nextstep.api.model.Account;
import com.nextstep.api.model.Group;
import com.nextstep.api.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/candidate")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CandidateController extends ABasicController{

    @PostMapping(value = "/signup", produces= MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody CandidateSignupForm candidateSignupForm, BindingResult bindingResult)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        apiMessageDto.setMessage("Sign Up Success");
        return apiMessageDto;
    }
}
