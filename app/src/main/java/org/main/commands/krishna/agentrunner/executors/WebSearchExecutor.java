package org.main.commands.krishna.agentrunner.executors;

import lombok.SneakyThrows;
import org.main.commands.krishna.agentrunner.AgentContext;
import org.main.commands.krishna.agentrunner.AgentExecutor;
import org.main.commands.krishna.agentrunner.models.ShortTermMemory;
import org.main.commands.krishna.agentrunner.models.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class WebSearchExecutor implements AgentExecutor {

    private final String BRAVE_ENDPOINT;
    private final String API_KEY;

    @Autowired
    public WebSearchExecutor(@Value("${app.brave.endpoint}") String braveEndpoint, @Value("${app.brave.apikey}") String braveApiKey) {
        this.BRAVE_ENDPOINT = braveEndpoint;
        this.API_KEY = braveApiKey;
    }

    @SneakyThrows
    @Override
    public void execute(String workflowId, AgentContext context) {
        Workflow workflow = context.get(workflowId);
        String userPrompt = workflow.getUserPrompt();

        String ENDPOINT = "https://api.search.brave.com/res/v1/web/search?q=";

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT + "/res/v1/web/search?q=" + URLEncoder.encode(userPrompt, StandardCharsets.UTF_8)))
                .header("X-Subscription-Token", API_KEY)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        context.save(workflowId, ShortTermMemory.builder()
                        .currentState("WEB_SEARCH")
                        .currentOutput(response.body())
                .build());
    }
}
