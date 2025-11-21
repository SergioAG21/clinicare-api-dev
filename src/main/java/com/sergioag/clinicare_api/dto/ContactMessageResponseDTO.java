package com.sergioag.clinicare_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergioag.clinicare_api.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ContactMessageResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String message;

    private String answer;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private MessageStatus status;
}
