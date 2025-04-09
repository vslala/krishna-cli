package org.main.chatbot;

import org.json.JSONObject;
import org.main.chatbot.domainmodels.Conversation;
import org.main.chatbot.domainmodels.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ConversationChatbot {


    // Simple enum for the main menu choices.
    private enum Command {
        START_NEW_CONVERSATION,
        LIST_CONVERSATION,
        EXIT
    }

    private final Scanner scanner;
    private final ClassicChatbot classicChatbot;
    private final ConversationManager conversationManager;

    @Autowired
    public ConversationChatbot(ClassicChatbot classicChatbot, ConversationManager conversationManager, Scanner scanner) {
        this.classicChatbot = classicChatbot;
        this.conversationManager = conversationManager;
        this.scanner = scanner;
    }

    /**
     * Entry point for the command-line chatbot.
     */
    public void startConversation() {
        Command command;
        do {
            command = showMainMenu();
            switch (command) {
                case START_NEW_CONVERSATION -> startNewConversation();
                case LIST_CONVERSATION -> continueExistingConversation();
                case EXIT -> System.out.println("Goodbye!");
            }
        } while (command != Command.EXIT);
    }

    /**
     * Displays the main menu and returns the selected command.
     */
    private Command showMainMenu() {
        System.out.println("""
                1. Start new conversation
                2. List conversations
                3. Exit
                """);
        String input = scanner.nextLine().trim();
        return switch (input) {
            case "1" -> Command.START_NEW_CONVERSATION;
            case "2" -> Command.LIST_CONVERSATION;
            default -> Command.EXIT;
        };
    }

    /**
     * Starts a new chat session.
     */
    private void startNewConversation() {
        Conversation currentConversation = conversationManager.startNewConversation();
        engageInConversation(currentConversation);
    }

    /**
     * Allows the user to pick from a list of recent sessions.
     */
    private void continueExistingConversation() {
        List<Conversation> conversations = conversationManager.listConversations(20);
        if (conversations.isEmpty()) {
            System.out.println("No existing conversations found. Starting a new conversation instead.");
            startNewConversation();
            return;
        }
        System.out.println("Available conversations:");
        conversations.forEach(conversation -> System.out.println(conversation.getId() + " - " + conversation.getTitle()));
        System.out.print("Enter the Session Id to continue: ");
        String sessionId = scanner.nextLine().trim();
        Optional<Conversation> sessionOptional = conversations.stream()
                .filter(conversation -> conversation.getId().equals(sessionId))
                .findFirst();
        if (sessionOptional.isPresent()) {
            Conversation currentConversation = sessionOptional.get();
            engageInConversation(currentConversation);
        } else {
            System.out.println("Session not found. Returning to main menu.");
        }
    }

    /**
     * Handles the conversation loop.
     */
    private void engageInConversation(Conversation currentConversation) {
        while (true) {
            System.out.println("Enter your userPrompt (type '<<<submit>>>' on a new line to finish, or type 'exit' on its own line to end conversation):");
            String userPrompt = readMultiLineInput();
            if ("exit".equalsIgnoreCase(userPrompt)) {
                finalizeConversation(currentConversation);
                break;
            }
            // Create a combined context (past conversation plus current userPrompt)
            String conversationContext = currentConversation.generateContext(
                    "You are an all-intelligent artificial intelligence here to support the user. " +
                            "Answer the following userPrompt with a thoughtful response."
            );
            String response = classicChatbot.input(conversationContext + userPrompt);
            Message userMessage = new Message(userPrompt, "user");
            Message assistantResponse = new Message(response, "assistant");

            currentConversation.addMessage(userMessage);
            currentConversation.addMessage(assistantResponse);
            conversationManager.updateConversation(currentConversation);
        }
    }

    /**
     * Reads multi-line input from the user until a terminator line is reached.
     */
    private String readMultiLineInput() {
        StringBuilder inputBuilder = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            // Check if the user wants to terminate input.
            if ("<<<submit>>>".equalsIgnoreCase(line.trim())) {
                break;
            }
            // If user types 'exit' on a line by itself, consider it a conversation exit.
            if ("exit".equalsIgnoreCase(line.trim())) {
                return "exit";
            }
            inputBuilder.append(line).append("\n");
        }
        return inputBuilder.toString().trim();
    }

    /**
     * Finalizes the conversation by asking the chatbot to generate a title.
     */
    private void finalizeConversation(Conversation currentConversation) {
        String conversationText = currentConversation.dumpConversation();
        String promptForTitle = String.format("""
                %s
                --
                Provide the best suited title for this conversation.
                
                Provide the contextual summarization of this conversation based on the messages so far.
                Keep all the essential information that has been discussed here so far, such that,
                if I share this summary with anyone they would be able to start the conversation
                fluently from that point.
                
                Strict OUTPUT FORMAT with no other unnecessary text and NO NEW LINE in a single paragraph:
                {
                    "title": "<Conversation title>",
                    "summary": "<Contextual Summary>
                }
                """, conversationText);
        var json = new JSONObject(classicChatbot.input(promptForTitle));
        String title = json.getString("title");
        String summary = json.getString("summary");
        conversationManager.finalize(currentConversation.withTitle(title).withSummary(summary));

        System.out.println("Conversation titled: " + title);
    }
}
