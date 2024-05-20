package org.chatbot;

import org.chatbot.models.ChatSession;
import org.chatbot.models.MessageExchange;
import org.containers.PythonContainerRunner;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeRunnerChatbot {
    private ChatSessionManager sessionManager;
    private final ClassicChatbot classicChatbot;
    private ChatSession chatSession = null;
    private PythonContainerRunner pythonContainerRunner;

    public CodeRunnerChatbot(String modelId) {
        classicChatbot = new ClassicChatbot(modelId);
        pythonContainerRunner = new PythonContainerRunner();
        sessionManager = new ChatSessionManager("/tmp/chat_sessions");
    }

    public String input(String prompt) {
        chatSession = sessionManager.createNewChatSession();
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
                String codeResponse = classicChatbot.input(chatSession.getConversation().toPrompt("").concat(systemPrompt));
                chatSession.addExchange(new MessageExchange(systemPrompt, codeResponse));
                pythonContainerRunner.build(parseRequirements(codeResponse), parsePythonCode(codeResponse));
                programOutput = pythonContainerRunner.run();
                if (programOutput.startsWith("ERROR")) {
                    String errorPrompt = "";
                    do {
                        String errorMessage = programOutput.split(":")[1];
                        errorPrompt = """
                                This is our conversation so far:
                                %s
                                
                                This is the code we ran:
                                %s
                                
                                This is response I got after executing the code:
                                %s
                             
                                I want you to try and fix the code so that we can re-run it to get the desired output.
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
                                """.formatted(chatSession.getConversation().pretty(), codeResponse, errorMessage);
                        codeResponse = classicChatbot.input(errorPrompt);
                        pythonContainerRunner.build(parseRequirements(codeResponse), parsePythonCode(codeResponse));
                        programOutput = pythonContainerRunner.run();
                    } while (programOutput.startsWith("ERROR"));
                    chatSession.addExchange(new MessageExchange(errorPrompt, programOutput));
                } else {
                    String finalResponse = classicChatbot.input(chatSession.getConversation().toPrompt("""
                            Following is the output from the code: 
                            ```Code Output
                            %s
                            ```
                            Make sure you use the Code Output to answer the user's prompt in natural language.
                            User Prompt: %s
                            """.formatted(programOutput, prompt))
                    );
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
                        You are an intelligent chatbot designed to assist users with their queries. 
                        When presented with a question, evaluate whether you need to run Python code to provide the most accurate result, or if you can answer it using your own knowledge. 
                        If running Python code will provide a better result, respond with 'Yes'. Otherwise, directly answer the query.
                        If the user query contains a link or the user provides a link, consider creating a web page crawler script in Python, execute it, and fetch the results for the user.
                        Useful information and tools at your disposal:
                            - OpenWeatherMap API Key: 46ed2f214d4e499619d261f4caf99ba1
                            
                        When responding, please indicate if you need to run Python code by saying 'Yes'. If you do not need to run Python code, proceed with your best knowledge to answer the query directly. If a link is provided, consider developing a web page crawler script to fetch the relevant information.
                        Before providing the Python code, always include the necessary packages in the requirements.txt format as follows:
                        ```requirements.txt
                        <package1>
                        <package2>
                        <package3>
                        ```
                        Make sure to always provide the requirements block whenever you are providing the code block.
                """.concat(System.lineSeparator());
        String finalPrompt = systemPrompt.concat("""
                ---------------------------
                Here's the user prompt: %s
                ---------------------------
                """.formatted(prompt));

        String response = classicChatbot.input(finalPrompt);
        chatSession.addExchange(new MessageExchange(finalPrompt, response));
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
