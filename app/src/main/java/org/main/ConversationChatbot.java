package org.main;

import org.chatbot.ChatSessionManager;
import org.chatbot.ClassicChatbot;
import org.chatbot.models.ChatSession;
import org.chatbot.models.MessageExchange;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConversationChatbot {
    private enum Command {
        INIT,
        START_NEW_CONVERSATION,
        LIST_CONVERSATION,
        CONTINUE_CONVERSATION, EXIT

    }

    private static final Scanner scan = new Scanner(System.in);
    private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
    private final BedrockRuntimeAsyncClient asyncBedrockClient = BedrockRuntimeAsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();
    private final ClassicChatbot classicChatbot = new ClassicChatbot(MODEL_ID);
    private static final String chatSessionDir = "/tmp/chat_sessions";
    private final ChatSessionManager sessionManager = new ChatSessionManager(chatSessionDir);
    private Command currCommand = Command.INIT;
    private ChatSession chatSession = null;

    private Command mainMenu() {
        String menuOptions = """
                1. Start new conversation
                2. List conversations
                3. Exit
                """;
        System.out.println(menuOptions);

        String input = scan.nextLine();
        return switch (input) {
            case "1" -> Command.START_NEW_CONVERSATION;
            case "2" -> Command.LIST_CONVERSATION;
            default -> Command.EXIT;
        };
    }

    public ConversationChatbot() {
        do {
            if (currCommand == Command.INIT) {
                currCommand = mainMenu();
            } else {
                switch (currCommand) {
                    case START_NEW_CONVERSATION -> {
                        chatSession = sessionManager.createNewChatSession();
                        currCommand = converse();
                    }
                    case CONTINUE_CONVERSATION -> {
                        currCommand = converse();
                    }
                    case LIST_CONVERSATION -> {
                        List<ChatSession> chatSessions = sessionManager.listLastNSessions(10);
                        currCommand = listConversation();
                    }
                }
            }
        } while (Command.EXIT != currCommand);
    }

    private Command listConversation() {
        List<ChatSession> chatSessions = sessionManager.listLastNSessions(10);
        System.out.println("Enter the Session Id from the list...");
        chatSessions.forEach(session -> System.out.println(session.getSessionId()));
        String sessionId = scan.nextLine();

        Optional<ChatSession> selectedChatSession = chatSessions.stream().filter(session -> session.getSessionId().equals(sessionId)).findFirst();
        if (selectedChatSession.isPresent()) {
            chatSession = selectedChatSession.get();
            return Command.CONTINUE_CONVERSATION;
        } else {
            return Command.LIST_CONVERSATION;
        }
    }

    private Command converse() {
        Command currCommand = Command.CONTINUE_CONVERSATION;
        do {
            System.out.print("Prompt: ");
            var multiLineInput = new StringBuilder();
            String line;
            while (!(line = scan.nextLine()).equals("<<<submit>>>")) {
                multiLineInput.append(line).append("\n");
            }
            if (multiLineInput.substring(multiLineInput.length() - 5).contains("exit")) {
                currCommand = Command.EXIT;
                String conversationTitle = classicChatbot.input("""
                        %s
                        --
                        Provide the best suited title for the conversation so that it can be used to name the conversation.
                        Make sure to output the title in snake case (word separated by _);
                        
                        OUTPUT FORMAT:
                        - ONLY spell out the title and no other word 
                        """.formatted(chatSession.getConversation().pretty()));
                chatSession.updateConversationTitle(conversationTitle);
                break;
            }
            String userPrompt = multiLineInput.toString();
            String conversationContext = chatSession.getConversation().toPrompt("You are an all intelligent artificial intelligence, here to support the user. The user will ask you questions, and you will provide responses");
            String response = classicChatbot.input(conversationContext + userPrompt);
            System.out.println();
            chatSession.addExchange(new MessageExchange(userPrompt, response));
        } while (currCommand != Command.EXIT);

        return currCommand;
    }

    private static Command exitOptions() {
        System.out.println("""
                Type "exit" to go back
                """);
        String userInput = scan.nextLine();

        return switch (userInput) {
            case "exit" -> Command.EXIT;
            default -> Command.CONTINUE_CONVERSATION;
        };
    }
}
