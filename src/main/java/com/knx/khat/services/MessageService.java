package com.knx.khat.services;

import com.knx.khat.dtos.MessageDTO;
import com.knx.khat.models.Chat;
import com.knx.khat.models.Message;
import com.knx.khat.models.User;
import com.knx.khat.repositories.ChatRepository;
import com.knx.khat.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    public MessageDTO sendMessage(String chatId, String content, User user) {
        Message message = new Message(content, user.getId());
        messageRepository.save(message);

        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isEmpty()) {
            System.out.println("Chat não encontrado");
            return null;
        }
        chat.get().getMessages().add(message);
        chatRepository.save(chat.get());

        return getMessageDTO(message, user);
    }


    private static MessageDTO getMessageDTO(Message message, User user) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setMessage(message.getMessage());
        messageDTO.setUser(user.getName());
        messageDTO.setUserId(user.getId());
        messageDTO.setData(message.getData());
        return messageDTO;
    }


}
