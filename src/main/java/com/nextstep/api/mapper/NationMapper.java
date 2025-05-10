package com.nextstep.api.mapper;

import com.nextstep.api.dto.nation.NationAdminDto;
import com.nextstep.api.dto.nation.NationDto;
import com.nextstep.api.form.nation.CreateNationForm;
import com.nextstep.api.form.nation.UpdateNationForm;
import com.nextstep.api.model.Nation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NationMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "postCode", target = "postCode")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToAutoCompleteDto")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToAdminDto")
    NationAdminDto fromEntityToAdminDto(Nation nation);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "postCode", target = "postCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateFormToEntity")
    Nation fromCreateFormToEntity(CreateNationForm createNationForm);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "postCode", target = "postCode")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("adminUpdateMapping")
    void fromUpdateFormToEntity(UpdateNationForm updateForm, @MappingTarget Nation nation);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "kind", target = "kind")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToAutoCompleteDto")
    NationDto fromEntityToAutoCompleteDto(Nation nation);

    @IterableMapping(elementTargetType = NationDto.class, qualifiedByName = "fromEntityToAutoCompleteDto")
    List<NationDto> convertToAutoCompleteDto(List<Nation> list);

    @IterableMapping(elementTargetType = NationAdminDto.class, qualifiedByName = "fromEntityToAdminDto")
    List<NationAdminDto> convertToListAdminDto(List<Nation> list);
}
