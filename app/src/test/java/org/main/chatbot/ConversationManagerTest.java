package org.main.chatbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.chatbot.domainmodels.Conversation;
import org.main.chatbot.mappers.ConversationMapper;
import org.main.chatbot.repositories.ConversationRepository;
import org.main.chatbot.repositories.entities.ConversationEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConversationManagerTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @InjectMocks
    private ConversationManager conversationManager;

    private Conversation conversation;
    private ConversationEntity conversationEntity;

    @BeforeEach
    public void setUp() {
        // Create a new Conversation instance (its constructor sets a UUID for id)
        conversation = new Conversation();

        // Create a corresponding ConversationEntity and set its id to the same value
        conversationEntity = new ConversationEntity();
        conversationEntity.setId(conversation.getId());
        conversationEntity.setTitle(conversation.getTitle());
        conversationEntity.setSummary(conversation.getSummary());
    }

    @Test
    public void testStartNewConversation() {
        // Arrange
        // When converting the domain model to entity, return our pre-built conversationEntity.
        when(conversationMapper.toEntity(any(Conversation.class))).thenReturn(conversationEntity);
        // When saving the entity, return the same conversationEntity.
        when(conversationRepository.save(any(ConversationEntity.class))).thenReturn(conversationEntity);

        // Act
        Conversation newConversation = conversationManager.startNewConversation();

        // Assert
        // Verify that the returned conversation is not null and has an id.
        assertNotNull(newConversation, "The conversation should not be null.");
        assertNotNull(newConversation.getId(), "The conversation id should not be null.");
        // Verify that the mapper and repository were called with the correct arguments.
        verify(conversationMapper).toEntity(newConversation);
        verify(conversationRepository).save(conversationEntity);
    }

    @Test
    public void testListConversations() {
        // Arrange
        List<ConversationEntity> entityList = new ArrayList<>();
        entityList.add(conversationEntity);
        when(conversationRepository.findAll()).thenReturn(entityList);

        List<Conversation> domainList = new ArrayList<>();
        domainList.add(conversation);
        when(conversationMapper.toDomainList(entityList)).thenReturn(domainList);

        // Act
        List<Conversation> conversations = conversationManager.listConversations(1);

        // Assert
        assertNotNull(conversations, "The conversation list should not be null.");
        assertEquals(1, conversations.size(), "The conversation list should contain exactly one conversation.");
        assertEquals(conversation.getId(), conversations.get(0).getId(), "The conversation id should match.");
        verify(conversationRepository).findAll();
        verify(conversationMapper).toDomainList(entityList);
    }

    @Test
    public void testFinalizeConversation() {
        // Arrange
        // The finalize method calls conversation.withSummary(...) to create a new conversation instance.
        String summaryText = "Use the current conversation exchanges to summarise the conversation";
        Conversation updatedConversation = conversation.withSummary(summaryText);

        // Create a ConversationEntity that corresponds to the updated conversation.
        ConversationEntity updatedEntity = new ConversationEntity();
        updatedEntity.setId(updatedConversation.getId());
        updatedEntity.setTitle(updatedConversation.getTitle());
        updatedEntity.setSummary(updatedConversation.getSummary());

        // When converting the updated conversation to entity, return our updatedEntity.
        when(conversationMapper.toEntity(updatedConversation)).thenReturn(updatedEntity);
        // When saving, return the updated entity.
        when(conversationRepository.save(updatedEntity)).thenReturn(updatedEntity);

        // Act
        conversationManager.finalize(updatedConversation);

        // Assert
        // Verify that the mapper was called with the updated conversation (with summary).
        verify(conversationMapper).toEntity(updatedConversation);
        // Verify that the repository saved the updated entity.
        verify(conversationRepository).save(updatedEntity);
    }
}

