package com.knx.khat.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ChatDTO {
    private String id;
    private String name;
    private List<MessageDTO> messages;
    private List<String> users;
    private String admin;
    private LocalDateTime creationDate;
    private LocalDateTime lastMessageDate;
}

