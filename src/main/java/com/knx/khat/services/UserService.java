package com.knx.khat.services;

import com.knx.khat.models.User;
import com.knx.khat.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static com.knx.khat.utils.EncryptionUtils.encryptSHA256;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public void registerUser(User user) {
        try {
            user.setPassword(encryptSHA256(user.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            return;
        }
        userRepository.save(user);
    }


}
