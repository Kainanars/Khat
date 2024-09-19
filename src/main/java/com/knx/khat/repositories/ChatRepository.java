package com.knx.khat.repositories;

import com.knx.khat.models.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {
    Optional<Chat> findById(String id);
}
