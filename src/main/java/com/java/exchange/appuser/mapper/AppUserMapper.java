package com.java.exchange.appuser.mapper;

import com.java.exchange.appuser.model.AppUser;
import com.java.exchange.dto.AppUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserDTO toDto(AppUser source);
}
