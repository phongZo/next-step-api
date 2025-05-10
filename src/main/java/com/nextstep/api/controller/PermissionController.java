package com.nextstep.api.controller;

import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.form.permission.CreatePermissionForm;
import com.nextstep.api.model.Permission;
import com.nextstep.api.repository.PermissionRepository;
import com.nextstep.api.exception.UnauthorizationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/permission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class PermissionController extends ABasicController{
    @Autowired
    PermissionRepository permissionRepository;

    @PostMapping(value = "/create", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PER_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreatePermissionForm createPermissionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Permission permission = permissionRepository.findFirstByName(createPermissionForm.getName());
        if(permission != null){
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Permission name is exist");
            return apiMessageDto;
        }
        permission = new Permission();
        permission.setName(createPermissionForm.getName());
        permission.setAction(createPermissionForm.getAction());
        permission.setDescription(createPermissionForm.getDescription());
        permission.setShowMenu(createPermissionForm.getShowMenu());
        permission.setNameGroup(createPermissionForm.getNameGroup());
        permission.setPCode(createPermissionForm.getPermissionCode());
        permissionRepository.save(permission);
        apiMessageDto.setMessage("Create permission success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PER_L')")
    public ApiMessageDto<List<Permission>> list() {
        ApiMessageDto<List<Permission>> apiMessageDto = new ApiMessageDto<>();
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed list.");
        }
        Page<Permission> accounts = permissionRepository.findAll(PageRequest.of(0, 1000, Sort.by(new Sort.Order(Sort.Direction.DESC, "createdDate"))));
        apiMessageDto.setData(accounts.getContent());
        apiMessageDto.setMessage("Get permissions list success");
        return apiMessageDto;
    }
}
