package com.nextstep.api.mapper;

import com.nextstep.api.dto.account.AccountAutoCompleteDto;
import com.nextstep.api.dto.account.AccountDto;
import com.nextstep.api.form.user.SignUpUserForm;
import com.nextstep.api.form.user.UpdateUserForm;
import com.nextstep.api.model.Account;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {GroupMapper.class})
public interface AccountMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "group", target = "group", qualifiedByName = "fromEntityToGroupDto")
    @Mapping(source = "lastLogin", target = "lastLogin")
    @Mapping(source = "avatarPath", target = "avatar")
    @Mapping(source = "isSuperAdmin", target = "isSuperAdmin")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromAccountToDto")
    AccountDto fromAccountToDto(Account account);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @Mapping(source = "fullName", target = "fullName")
    @Named("fromAccountToAutoCompleteDto")
    AccountAutoCompleteDto fromAccountToAutoCompleteDto(Account account);


    @IterableMapping(elementTargetType = AccountAutoCompleteDto.class)
    List<AccountAutoCompleteDto> convertAccountToAutoCompleteDto(List<Account> list);

    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    Account fromSignUpUserToAccount(SignUpUserForm signUpUserForm);


    @Mapping(source = "phone",target = "phone")
    @Mapping(source = "email",target = "email")
    @Mapping(source = "fullName",target = "fullName")
    @Mapping(source = "avatarPath",target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateUserFormToEntity(UpdateUserForm updateUserForm, @MappingTarget Account account );



}
