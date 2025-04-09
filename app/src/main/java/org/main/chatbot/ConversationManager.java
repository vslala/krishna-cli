package org.main.chatbot;

import org.main.chatbot.domainmodels.Conversation;
import org.main.chatbot.mappers.ConversationMapper;
import org.main.chatbot.repositories.ConversationRepository;
import org.main.chatbot.repositories.entities.ConversationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationManager {

    private final ConversationMapper conversationMapper;
    private final ConversationRepository conversationRepository;

    @Autowired
    public ConversationManager(ConversationRepository conversationRepository, ConversationMapper conversationMapper) {
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
    }

    public Conversation startNewConversation() {
        Conversation conversation = new Conversation();
        this.conversationRepository.save(this.conversationMapper.toEntity(conversation));
        return conversation;
    }

    public List<Conversation> listConversations(int lastNSession) {
        List<ConversationEntity> conversationEntities = conversationRepository.findAll();
        return this.conversationMapper.toDomainList(conversationEntities);
    }

    public void finalize(Conversation conversation) {
        ConversationEntity conversationEntity = conversationMapper.toEntity(conversation);
        this.conversationRepository.save(conversationEntity);
    }

    public void updateConversation(Conversation conversation) {
        var conversationEntity = conversationMapper.toEntity(conversation);
        conversationRepository.save(conversationEntity);
    }
}
