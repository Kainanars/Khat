package com.knx.khat.controllers;

import com.knx.khat.dtos.AuthResponse;
import com.knx.khat.dtos.LoginDTO;
import com.knx.khat.dtos.MessageResponse;
import com.knx.khat.dtos.UserDTO;
import com.knx.khat.models.User;
import com.knx.khat.services.UserService;
import com.knx.khat.utils.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getPassword() == null || userDTO.getName() == null) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("Campos obrigatórios não preenchidos");
            return ResponseEntity.badRequest().body(messageResponse);
        }
        Optional<User> userExists = userService.findByEmail(userDTO.getEmail());
        if (userExists.isPresent()) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("Email já cadastrado");
            return ResponseEntity.badRequest().body(messageResponse);
        }

        User user = new User(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());
        userService.registerUser(user);
        if (user.getId() == null) {
            return ResponseEntity.internalServerError().body("Erro ao cadastrar usuário");
        }

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Usuário cadastrado com sucesso");
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDto, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

            // Configura o cookie HTTP com o token JWT
            Cookie cookie = new Cookie("token", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // Expiração em 1 dia
            response.addCookie(cookie);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(jwt);
            authResponse.setMessage("Login efetuado com sucesso");
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("Email ou senha inválidos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expirar imediatamente
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout efetuado com sucesso");
    }

}
