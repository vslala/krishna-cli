package org.main.chatbot;

import org.main.chatbot.domainmodels.Conversation;
import org.main.chatbot.domainmodels.Message;
import org.main.containers.PythonContainerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CodeRunnerChatbot {
    private ConversationManager sessionManager;
    private final ClassicChatbot classicChatbot;
    private Conversation chatSession = null;
    private PythonContainerRunner pythonContainerRunner;

    @Autowired
    public CodeRunnerChatbot(ClassicChatbot classicChatbot, ConversationManager conversationManager) {
        this.classicChatbot = classicChatbot;
        pythonContainerRunner = new PythonContainerRunner();
        this.sessionManager = conversationManager;
    }

    public String input(String prompt) {
        chatSession = sessionManager.startNewConversation();
        String response = checkIfCodeExecutionRequired(prompt);
        String programOutput = "";
        if (response.contains("Yes")) {
            do {
                String systemPrompt = """
                        Please provide me the python code that you need to execute in the following format:
                        ```python
                        <code>
                        ```
                        Before providing the Python code, always include the necessary packages in the requirements.txt format as follows:
                        ```requirements.txt
                        <package1>
                        <package2>
                        <package3>
                        ```
                        Make sure to always provide the requirements block whenever you are providing the code block.
                        """;
                String codeResponse = classicChatbot.input(chatSession.generateContext(systemPrompt));
                chatSession.addMessage(new Message(systemPrompt, codeResponse));
                pythonContainerRunner.build(parseRequirements(codeResponse), parsePythonCode(codeResponse));
                programOutput = pythonContainerRunner.run();
                System.out.println("<<<<<<<<<<<PROGRAM OUTPUT START>>>>>>>>>>>>>>>>>>>>");
                System.out.println(programOutput);
                System.out.println("<<<<<<<<<<<PROGRAM OUTPUT END>>>>>>>>>>>>>>>>>>>>");
                if (programOutput.startsWith("ERROR")) {
                    String errorPrompt = "";
                    do {
                        String errorMessage = programOutput.split(":")[1];
                        errorPrompt = """
                                %s
                                
                                User ran the following code:
                                %s
                                
                                User got following response after executing the above code:
                                %s
                             
                                I want you to try and fix the code so that user can re-run it to get the desired output.
                                Please provide the modified python code with no errors to execute in the following format:
                                ```python
                                <code>
                                ```
                                Before providing the Python code, always include the necessary packages in the requirements.txt format as follows:
                                ```requirements.txt
                                <package1>
                                <package2>
                                <package3>
                                ```
                                Make sure to always provide the requirements block whenever you are providing the code block.
                                """.formatted(chatSession.dumpConversation(), codeResponse, errorMessage);
                        codeResponse = classicChatbot.input(errorPrompt);
                        pythonContainerRunner.build(parseRequirements(codeResponse), parsePythonCode(codeResponse));
                        programOutput = pythonContainerRunner.run();
                    } while (programOutput.startsWith("ERROR"));
                    chatSession.addMessage(new Message(errorPrompt, programOutput));
                } else {
                    String finalResponse = classicChatbot.input(chatSession.generateContext(
                            """
                            User ran the code and it ran successfully. This is the output from the code, use this output to provide a useful response to the user's prompt:
                            ```Code Output
                            %s
                            ```
                            Now you have the conversation context, you may use it to answer the user's prompt in natural language.
                            """.formatted(programOutput)
                    ));
                    return finalResponse;
                }
            } while (true);

        }

        return response;
    }

    private String parseRequirements(String requirements) {
        // Regular expression to find the Python code block
        String regex = "(?s)```requirements.txt\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(requirements);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return """
                    requests
                    """;
        }
    }

    private String parsePythonCode(String code) {
        // Regular expression to find the Python code block
        String regex = "(?s)```python\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "No Python code block found.";
        }
    }

    private String checkIfCodeExecutionRequired(String prompt) {
        String systemPrompt = """
                        # IDENTITY AND PURPOSE
                        
                        You are an intelligent assistant with the ability to execute Python code and fetch information from the web. When a user asks a question, follow these steps:
                        
                        # STEPS 
                        
                        1. Analyze the query to determine if you can provide a satisfactory answer using your existing knowledge base. If so, directly respond with the answer.
                        2. If the query requires additional computation, web scraping, or API calls, respond with "Yes, I can execute Python code to assist with this query."
                        3. If you respond with "Yes", provide the necessary code in the following format:
                        
                        ```requirements.txt
                        <package1>
                        <package2>
                        ...
                        ```
                        
                        ```python
                        # Add your Python code here
                        ```
                        
                        # Notes:
                        - Exclude common Python libraries like os, sys, math, etc. from the requirements.txt, as they are already part of the Python runtime.
                        - If the query involves fetching information from a website, consider creating a web scraper script.
                        - Use free web APIs that doesn't require any authentication first before scraping the web for information. For example: prefer using `yFinance` for fetching financial details rather than scraping yahoo website.
                        - If the query requires using an API, include the necessary API key(s) or authentication details in the code.
                        - You have access to the OpenWeatherMap API Key: 46ed2f214d4e499619d261f4caf99ba1
                        - You have access to google search API Key: AIzaSyBFvKwK3fIDHKFyNYxiRBO3g4mUJp9xG4A
                        
                        4. After providing the code, execute it and share the output or results with the user.
                        5. If you cannot provide a satisfactory answer or code solution, politely indicate that you do not have enough information or capabilities to assist with the specific query.
                """.concat(System.lineSeparator());
        String finalPrompt = systemPrompt.concat("""
                ---------------------------
                Here's the user prompt: %s
                ---------------------------
                """.formatted(prompt));

        String response = classicChatbot.input(finalPrompt);
        chatSession.addMessage(new Message(finalPrompt, response));
        return response;
    }

//    public static void main(String[] args) {
//        var scan = new Scanner(System.in);
//        var chatbot = new CodeRunnerChatbot("anthropic.claude-3-sonnet-20240229-v1:0");
//        String userPrompt;
//        do {
//            System.out.print("\nPrompt:\t");
//            userPrompt = scan.nextLine();
//            chatbot.input(userPrompt);
//        } while (!"exit".equals(userPrompt));
//    }
}
