package org.main.chatbot;

import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.main.chatbot.domainmodels.Message;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class ChatbotWithToolsTest {

    private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
    private static final String LLM_PYTHON_CODE_EXECUTION_RESPONSE = """
            ```json
            {
                "tool_required": true,
                "tool_id": "python_code_execution"
            }
            ```
            
            <MainResponse>            
            #### Python Code
            ```python
            import yfinance as yf
                        
            # Get the latest stock data for Amazon
            amzn = yf.Ticker("AMZN")
            amzn_info = amzn.info
                        
            # Extract the current share price
            share_price = amzn_info["regularMarketPrice"]
                        
            print(f"The current share price of Amazon is: ${share_price:.2f}")
            ```
                        
            #### Library Requirements
            ```text
            yfinance
            ```
                        
            This code uses the yfinance library to fetch the latest stock data for Amazon (ticker symbol "AMZN"). It then extracts the current share price from the retrieved data and prints it out in a formatted string.
            </MainResponse>            
            """;
    private final BedrockRuntimeAsyncClient asyncBedrockClient = BedrockRuntimeAsyncClient.builder().region(Region.US_EAST_1).build();
    private final ClassicChatbot classicChatbot = new ClassicChatbot(MODEL_ID, asyncBedrockClient);

    @Test
    void it_should_take_user_prompt_and_select_one_of_the_available_tools_or_simply_return_the_output_in_the_desired_format() {
        var chatbot = new ChatbotWithTools(this.classicChatbot);
        Message message = chatbot.input("What is the share price of Amazon?");
        assertNotNull(message);
        System.out.println(message);
    }

    @Test
    void it_should_parse_the_generic_response() {
        var codeExecutionTool = new ChatbotWithTools.PythonCodExecutionTool();
        boolean isRequired = codeExecutionTool.isRequired(LLM_PYTHON_CODE_EXECUTION_RESPONSE);
        assertTrue(isRequired);
    }

    @Test
    void it_should_build_and_run_python_docker_container() {
        var codeExecutionTool = new ChatbotWithTools.PythonCodExecutionTool();
        JSONObject codeExecution = codeExecutionTool.execute(LLM_PYTHON_CODE_EXECUTION_RESPONSE);

        System.out.println(codeExecution.toString(4));
        assertTrue(codeExecution.getBoolean("error"));
    }

}
