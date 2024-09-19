package com.knx.khat.configs;

import com.knx.khat.utils.EncryptionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;

public class SHA256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return EncryptionUtils.encryptSHA256(rawPassword.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar a senha", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            return EncryptionUtils.encryptSHA256(rawPassword.toString()).equals(encodedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao verificar a senha", e);
        }
    }
}
