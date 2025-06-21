package com.nextstep.api.dto.permission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class PermissionDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "action")
    private String action;
    @ApiModelProperty(name = "showMenu")
    private Boolean showMenu;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "nameGroup")
    private String nameGroup;
    @ApiModelProperty(name = "PCode")
    private String PCode;
}
