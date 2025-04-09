package org.main.chatbot.repositories;

import org.main.chatbot.repositories.entities.ConversationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends CrudRepository<ConversationEntity, String> {
    Optional<ConversationEntity> findById(ConversationEntity conversation);
    List<ConversationEntity> findAll();
}
