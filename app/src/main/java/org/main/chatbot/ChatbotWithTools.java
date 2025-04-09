package org.main.chatbot;

import org.json.JSONObject;
import org.main.chatbot.domainmodels.Message;
import org.main.containers.PythonContainerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ChatbotWithTools {

    private final List<Tool> AVAILABLE_TOOLS = List.of(
            new PythonCodExecutionTool()
    );
    private final ClassicChatbot chatbot;


    @Autowired
    public ChatbotWithTools(ClassicChatbot classicChatbot) {
        this.chatbot = classicChatbot;
    }


    public Message input(String prompt) {
        var messages = new ArrayList<org.models.Message>();

        InputStream availableToolsPromptInputStream = getClass().getResourceAsStream("/agent_prompts/select_from_available_tools/system.md");
        assert availableToolsPromptInputStream != null;

        var sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(availableToolsPromptInputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sb.append(prompt);

        messages.add(new org.models.Message(sb.toString(), LocalDateTime.now(), "SystemPrompt"));
        messages.add(new org.models.Message(prompt, LocalDateTime.now(), "User"));

        String response = this.chatbot.input(sb.toString());

        messages.add(new org.models.Message(response, LocalDateTime.now(), "Chatbot"));

        Optional<Tool> selectedTool = AVAILABLE_TOOLS.stream()
                .filter(tool -> tool.isRequired(response))
                .findFirst();

        if (selectedTool.isPresent()) {
            JSONObject executionResponse;
            do {
                executionResponse = selectedTool.get().execute(response);
                messages.add(new org.models.Message(executionResponse.toString(4), LocalDateTime.now(), selectedTool.get().getId()));
                if (executionResponse.getString("status").equalsIgnoreCase("failed")) {
                    System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    String llmResponse = chatbot.input(messages.toString());
                    messages.add(new org.models.Message(llmResponse, LocalDateTime.now(), "Chatbot"));
                }
            } while (executionResponse.getString("status").equalsIgnoreCase("success"));

        }
        return new Message(prompt, response);
    }


    interface ToolExecution {
        JSONObject execute(String llmResponse);
    }

    interface ToolRequirement {
        boolean isRequired(String llmResponse);
    }

    interface Tool extends ToolExecution, ToolRequirement {
        String getId();
    }

    public static class PythonCodExecutionTool implements Tool {

        @Override
        public JSONObject execute(String llmResponse) {
            String chunk = llmResponse.substring(llmResponse.indexOf("<MainResponse>"), llmResponse.indexOf("</MainResponse>"));
            String pythonCodeChunk = Util.parsePythonCode(chunk);
            String requirements = Util.parseTextBlock(chunk);

            var pythonDockerContainer = new PythonContainerRunner();
            pythonDockerContainer.build(requirements, pythonCodeChunk);
            String codeExecutionResult = pythonDockerContainer.run();

            var output = new JSONObject();
            output.put("status", codeExecutionResult.startsWith("ERROR") ? "FAILED" : "SUCCESS");
            output.put("response", codeExecutionResult);
            output.put("code_executed", pythonCodeChunk);

            System.out.println("OUTPUT: " + codeExecutionResult);
            return output;
        }

        @Override
        public boolean isRequired(String llmResponse) {
            JSONObject genericResponse = parseGenericResponse(llmResponse);
            return genericResponse.getBoolean("tool_required");
        }

        private JSONObject parseGenericResponse(String llmResponse) {
            String chunk = llmResponse.substring(0, llmResponse.indexOf("<MainResponse>"));
            String genericResponse = Util.parseJSONBlock(chunk);
            return new JSONObject(genericResponse);
        }

        @Override
        public String getId() {
            return "python_code_execution_agent";
        }
    }
}
