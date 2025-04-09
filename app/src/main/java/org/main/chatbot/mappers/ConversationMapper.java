package org.main.chatbot.mappers;

import org.main.chatbot.domainmodels.Conversation;
import org.main.chatbot.repositories.entities.ConversationEntity;
import org.main.chatbot.repositories.entities.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConversationMapper {

    private final MessageMapper messageMapper;

    @Autowired
    private ConversationMapper(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public ConversationEntity toEntity(Conversation conversation) {
        ConversationEntity conversationEntity = new ConversationEntity();
        conversationEntity.setId(conversation.getId());
        conversationEntity.setTitle(conversation.getTitle());
        List<MessageEntity> messageEntities = this.messageMapper.toEntityList(conversation.getMessages());
        messageEntities.forEach(messageEntity -> messageEntity.setConversation(conversationEntity));
        conversationEntity.setMessages(messageEntities);
        conversationEntity.setSummary(conversation.getSummary());
        return conversationEntity;
    }

    public Conversation toDomain(ConversationEntity conversationEntity) {
        Conversation conversation = new Conversation();
        conversation.setId(conversationEntity.getId());
        conversation.setTitle(conversationEntity.getTitle());
        conversation.setMessages(this.messageMapper.toDomainList(conversationEntity.getMessages()));
        conversation.setSummary(conversationEntity.getSummary());
        return conversation;
    }


    public List<Conversation> toDomainList(List<ConversationEntity> conversationEntities) {
        return conversationEntities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
