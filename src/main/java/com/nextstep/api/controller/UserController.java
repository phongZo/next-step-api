package com.nextstep.api.controller;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.ErrorCode;
import com.nextstep.api.dto.ResponseListDto;
import com.nextstep.api.dto.user.UserAutoCompleteDto;
import com.nextstep.api.dto.user.UserDto;
import com.nextstep.api.form.user.SignUpUserForm;
import com.nextstep.api.form.user.LoginForm;
import com.nextstep.api.form.user.UpdateUserForm;
import com.nextstep.api.mapper.AccountMapper;
import com.nextstep.api.mapper.UserMapper;
import com.nextstep.api.model.Account;
import com.nextstep.api.model.Group;
import com.nextstep.api.model.User;
import com.nextstep.api.model.criteria.UserCriteria;
import com.nextstep.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private GroupRepository groupRepository;


    @Autowired
    private ServiceRepository serviceRepository;

    @PostMapping(value = "/signup", produces= MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody SignUpUserForm signUpUserForm, BindingResult bindingResult)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

            Account accountByPhone = accountRepository.findAccountByPhone(signUpUserForm.getPhone());
            if (accountByPhone!=null)
            {
                apiMessageDto.setMessage("phone number already exists");
                apiMessageDto.setCode(ErrorCode.USER_ERROR_EXIST);
                apiMessageDto.setResult(false);
                return apiMessageDto;
            }

        if (signUpUserForm.getEmail()!=null)
        {
            Account accountByEmail = accountRepository.findAccountByEmail(signUpUserForm.getEmail());
            if (accountByEmail!=null)
            {
                apiMessageDto.setMessage("email already exists");
                apiMessageDto.setCode(ErrorCode.USER_ERROR_EXIST);
                apiMessageDto.setResult(false);
                return apiMessageDto;
            }
        }
        Account account = accountMapper.fromSignUpUserToAccount(signUpUserForm);
        account.setPassword(passwordEncoder.encode(signUpUserForm.getPassword()));
        account.setKind(NextStepConstant.USER_KIND_USER);
        Group group = groupRepository.findFirstByKind(NextStepConstant.GROUP_KIND_USER);
        account.setGroup(group);
        account.setStatus(NextStepConstant.STATUS_ACTIVE);
        accountRepository.save(account);

        User user = new User();
        user.setAccount(account);
        user.setBirthday(signUpUserForm.getBirthday());
        user.setStatus(NextStepConstant.STATUS_ACTIVE);
        userRepository.save(user);
        apiMessageDto.setMessage("Sign Up Success");
        return apiMessageDto;
    }

    @PostMapping(value = "/login", produces= MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> login(@Valid @RequestBody LoginForm loginForm, BindingResult bindingResult)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Account account = accountRepository.findAccountByPhone(loginForm.getPhone());
        if (account==null||!passwordEncoder.matches((loginForm.getPassword()),account.getPassword()))
        {
            apiMessageDto.setMessage("phone number or password is not correct ");
            apiMessageDto.setCode(ErrorCode.USER_ERROR_LOGIN_FAILED);
            apiMessageDto.setResult(false);
            return apiMessageDto;
        }
        apiMessageDto.setMessage("Login Success");
        return apiMessageDto;
    }
    @GetMapping(value = "/get/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_V')")
    public ApiMessageDto<UserDto> getUser(@PathVariable("id") Long id)
    {
        ApiMessageDto<UserDto> apiMessageDto = new ApiMessageDto<>();
        User user = userRepository.findById(id).orElse(null);
        if (user==null)
        {
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not found user");
            apiMessageDto.setCode(ErrorCode.USER_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        apiMessageDto.setData(userMapper.fromEntityToUserDto(user));
        apiMessageDto.setMessage("get user success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('US_D')")
    public ApiMessageDto<String> deleteUser(@PathVariable("id") Long id)
    {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        User user = userRepository.findById(id).orElse(null);
        if (user==null)
        {
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not found user");
            apiMessageDto.setCode(ErrorCode.USER_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        Account account = accountRepository.findById(user.getAccount().getId()).orElse(null);
        if (account.getIsSuperAdmin()){
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not allow delete super admin");
            apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_NOT_ALLOW_DELETE_SUPPER_ADMIN);
            return apiMessageDto;
        }
        userRepository.delete(user);
        serviceRepository.deleteAllByAccountId(account.getId());
        accountRepository.delete(account);
        apiMessageDto.setMessage("Delete User success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_L')")
    public ApiMessageDto<ResponseListDto<List<UserDto>>> getList(UserCriteria userCriteria , Pageable pageable)
    {

        ApiMessageDto<ResponseListDto<List<UserDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<UserDto>> responseListDto = new ResponseListDto<>();
        Page<User> listUser = userRepository.findAll(userCriteria.getSpecification(),pageable);
        responseListDto.setContent(userMapper.fromUserListToUserDtoList(listUser.getContent()));
        responseListDto.setTotalPages(listUser.getTotalPages());
        responseListDto.setTotalElements(listUser.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list user success");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete",produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<UserAutoCompleteDto>>> ListAutoComplete(UserCriteria userCriteria)
    {
        ApiMessageDto<ResponseListDto<List<UserAutoCompleteDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<UserAutoCompleteDto>> responseListDto = new ResponseListDto<>();
        Pageable pageable = PageRequest.of(0,10);
        Page<User> listUser =userRepository.findAll(userCriteria.getSpecification(),pageable);
        responseListDto.setContent(userMapper.fromUserListToUserDtoListAutocomplete(listUser.getContent()));
        responseListDto.setTotalPages(listUser.getTotalPages());
        responseListDto.setTotalElements(listUser.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("get success");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('US_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateUserForm updateUserForm, BindingResult bindingResult) {

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        User user = userRepository.findById(updateUserForm.getId()).orElse(null);
        if (user==null)
        {
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Not found user");
            apiMessageDto.setCode(ErrorCode.USER_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        if (!user.getAccount().getPhone().equalsIgnoreCase(updateUserForm.getPhone()))
        {
            Account account = accountRepository.findAccountByPhone(updateUserForm.getPhone());
            if(account!=null)
            {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.USER_ERROR_EXIST);
                apiMessageDto.setMessage("Phone is existed");
                return apiMessageDto;
            }
        }
        if (!user.getAccount().getEmail().equalsIgnoreCase(updateUserForm.getEmail()))
        {
            Account account = accountRepository.findAccountByEmail(updateUserForm.getEmail());
            if(account!=null)
            {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.USER_ERROR_EXIST);
                apiMessageDto.setMessage("Email is existed");
                return apiMessageDto;
            }

        }
        Account account = accountRepository.findById(user.getAccount().getId()).orElse(null);
        if(StringUtils.isNoneBlank(updateUserForm.getPassword()))
        {
            account.setPassword(passwordEncoder.encode(updateUserForm.getPassword()));
        }
        accountMapper.fromUpdateUserFormToEntity(updateUserForm,account);
        accountRepository.save(account);
        apiMessageDto.setMessage("update success");
        return apiMessageDto;
    }





}
