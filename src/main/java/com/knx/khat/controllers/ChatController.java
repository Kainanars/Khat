package com.knx.khat.controllers;

import com.knx.khat.configs.JwtAuthenticationFilter;
import com.knx.khat.dtos.ChatDTO;
import com.knx.khat.dtos.CreateChatDTO;
import com.knx.khat.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @GetMapping("/chats")
    public String listChats(Model model) {
        List<ChatDTO> chats = chatService.getAllChats();
        model.addAttribute("chats", chats);
        return "chats";
    }

    @PostMapping("/api/chats")
    public ResponseEntity<?> createChat(@RequestBody CreateChatDTO createChatDTO) {
        if (createChatDTO.getName() == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        ChatDTO chat = chatService.createChat(createChatDTO.getName(), email);
        if (chat == null) {
            return ResponseEntity.badRequest().body("Error creating chat");
        }

        return ResponseEntity.ok(chat);
    }
}
