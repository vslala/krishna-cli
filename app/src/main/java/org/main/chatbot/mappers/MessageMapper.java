package org.main.chatbot.mappers;

import org.main.chatbot.domainmodels.Message;
import org.main.chatbot.repositories.entities.MessageEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {
    public Message toDomain(MessageEntity messageEntity) {
        return new Message(messageEntity.getId(), messageEntity.getText(), messageEntity.getActor());
    }

    public MessageEntity toEntity(Message message) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(message.getId());
        messageEntity.setText(message.getText());
        messageEntity.setActor(message.getActor());
        return messageEntity;
    }

    public List<Message> toDomainList(List<MessageEntity> messageEntities) {
        return messageEntities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    public List<MessageEntity> toEntityList(List<Message> messages) {
        return messages.stream().map(this::toEntity).collect(Collectors.toList());
    }
}

