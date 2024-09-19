package com.knx.khat.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    private List<Message> messages;
    private List<String> users;
    private String name;
    private String admin;
    private LocalDateTime creationDate;
    private LocalDateTime lastMessageDate;

    public Chat(String name, String admin, List<String> users ) {
        this.name = name;
        this.users = users;
        this.admin = admin;
        this.messages = new ArrayList<>();
        this.creationDate = LocalDateTime.now();
    }
}
