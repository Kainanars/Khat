package com.knx.khat.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessageDTO {
    private String chatId;
    private String message;
    private String userId;
}
