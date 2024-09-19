package com.knx.khat.controllers;

import com.knx.khat.dtos.ChatDTO;
import com.knx.khat.dtos.ChatMessageDTO;
import com.knx.khat.dtos.MessageDTO;
import com.knx.khat.models.User;
import com.knx.khat.services.ChatService;
import com.knx.khat.services.MessageService;
import com.knx.khat.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class MessageController {


    private final MessageService messageService;
    private final ChatService chatService;
    private final UserService userService;
    @Value("${app.secretKey}")
    private String secretKey;

    public MessageController(MessageService messageService, ChatService chatService, UserService userService) {
        this.messageService = messageService;
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping("/chat/{id}")
    public String chat(@PathVariable String id, Model model) {
        User user = getUser();
        if (user == null) {
            return "redirect:/login";
        }
        ChatDTO chatDTO = chatService.getChat(id);
        if (chatDTO == null) {
            return "chats";
        }
        if (chatDTO.getUsers().stream().noneMatch(userId -> userId.equals(user.getId()))) {
            chatDTO = chatService.addUserToChat(id, user.getEmail());
        }
        if (chatDTO.getAdmin().equals(user.getId())) {
            model.addAttribute("isAdmin", true);
        }


        model.addAttribute("chat", chatDTO);
        model.addAttribute("messages", chatDTO.getMessages());
        model.addAttribute("userInfo", user.getId());
        model.addAttribute("secretKey", secretKey);
        return "chat";
    }

    @MessageMapping("/chat/send")
    @SendTo("/topic/public")
    public ResponseEntity<?> sendMessage(ChatMessageDTO chatMessageDTO) {
        User user = getUserByID(chatMessageDTO.getUserId());
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        MessageDTO messageDTO = messageService.sendMessage(chatMessageDTO.getChatId(), chatMessageDTO.getMessage(), user);
        if (messageDTO == null) {
            return ResponseEntity.badRequest().body("Error sending message");
        }
        return ResponseEntity.ok(messageDTO);
    }


    @PostMapping("/api/chat/{id}/addUser")
    public ResponseEntity<?> addUserToChat(@PathVariable String id) {
        User user = getUser();
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        ChatDTO chatDTO = chatService.addUserToChat(id, user.getEmail());
        if (chatDTO == null) {
            return ResponseEntity.badRequest().body("Error adding user to chat");
        }
        return ResponseEntity.ok(chatDTO);
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userService.findByEmail(email);
        return user.orElse(null);
    }

    private User getUserByID(String userId) {
        Optional<User> user = userService.findById(userId);
        return user.orElse(null);
    }
}
