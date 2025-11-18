package com.sergioag.clinicare_api.mapper;

import com.sergioag.clinicare_api.dto.ContactMessageResponseDTO;
import com.sergioag.clinicare_api.entity.ContactMessage;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ContactMessageMapper {
    ContactMessageResponseDTO toMessageResponseDTO(ContactMessage contactMessage);

    List<ContactMessageResponseDTO> toMessageResponseDTOs(List<ContactMessage> contactMessages);
}
