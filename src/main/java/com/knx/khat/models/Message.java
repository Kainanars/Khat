package com.knx.khat.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String message;
    private String user;
    private LocalDateTime data;

    public Message(String message, String user) {
        this.message = message;
        this.user = user;
        this.data = LocalDateTime.now();    }

}
