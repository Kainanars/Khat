package com.knx.khat.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {
    private String id;
    private String message;
    private String user;
    private String userId;
    private LocalDateTime data;
}