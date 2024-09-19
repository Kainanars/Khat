package com.knx.khat.services;

import com.knx.khat.dtos.ChatDTO;
import com.knx.khat.dtos.MessageDTO;
import com.knx.khat.models.Chat;
import com.knx.khat.models.Message;
import com.knx.khat.models.User;
import com.knx.khat.repositories.ChatRepository;
import com.knx.khat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public ChatDTO createChat(String name, String adminEmail) {
        Optional<User> admin = userRepository.findByEmail(adminEmail);
        if (admin.isEmpty()) {
            System.out.println("Admin não encontrado");
            return null;
        }
        List<String> users = new ArrayList<>();
        users.add(admin.get().getId());

        Chat chat = new Chat(name, admin.get().getId(), users);
        chatRepository.save(chat);
        System.out.println("Chat criado com sucesso");
        return chatToChatDTO(chat);
    }

    public ChatDTO getChat(String id) {
        Optional<Chat> chat = chatRepository.findById(id);
        if (chat.isEmpty()) {
            System.out.println("Chat não encontrado");
            return null;
        }
        return chatToChatDTO(chat.get());
    }

    public ChatDTO addUserToChat(String chatId, String userEmail) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isEmpty()) {
            System.out.println("Chat não encontrado");
            return null;
        }
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            System.out.println("Usuário não encontrado");
            return null;
        }
        chat.get().getUsers().add(user.get().getId());
        chatRepository.save(chat.get());
        return chatToChatDTO(chat.get());
    }



    private ChatDTO chatToChatDTO(Chat chat) {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setId(chat.getId());
        chatDTO.setName(chat.getName());
        chatDTO.setAdmin(chat.getAdmin());
        chatDTO.setMessages(new ArrayList<>());
        for (Message message : chat.getMessages()) {
            chatDTO.getMessages().add(getMessageDTO(message));
        }
        chatDTO.setCreationDate(chat.getCreationDate());
        chatDTO.setLastMessageDate(chat.getLastMessageDate());
        chatDTO.setUsers(chat.getUsers());
        return chatDTO;
    }

    private MessageDTO getMessageDTO(Message message) {
        User user = userRepository.findById(message.getUser()).orElse(null);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setMessage(message.getMessage());
        assert user != null;
        messageDTO.setUser(user.getName());
        messageDTO.setUserId(user.getId());
        messageDTO.setData(message.getData());
        return messageDTO;
    }

    public List<ChatDTO> getAllChats() {
        List<Chat> chats = chatRepository.findAll();
        List<ChatDTO> chatDTOs = new ArrayList<>();
        for (Chat chat : chats) {
            chatDTOs.add(chatToChatDTO(chat));
        }
        return chatDTOs;
    }
}
