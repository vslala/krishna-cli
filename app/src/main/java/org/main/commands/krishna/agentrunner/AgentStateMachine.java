package org.main.commands.krishna.agentrunner;

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class AgentStateMachine {
    private JSONArray llmThoughts;

    public StateMachine<String, String> build(String llmThoughts) throws Exception {
        this.llmThoughts = new JSONArray(llmThoughts);
        var builder = new StateMachineBuilder.Builder<String, String>();
        builder.configureStates()
                .withStates()
                .initial("INIT")
                .end("END")
                .states(generateStates());

        configureStateTransitions(builder);

        return builder.build();
    }

    @SneakyThrows
    private void configureStateTransitions(StateMachineBuilder.Builder<String, String> builder) {
        String initialThought = llmThoughts.getJSONObject(0).getString("action");

        var transitions = builder.configureTransitions();
        transitions
                .withExternal()
                .source("INIT")
                .target(initialThought)
                .event("TO_" + initialThought)
                .and();

        for (int i = 0; i < llmThoughts.length() - 1; i++) {
            JSONObject currThought = llmThoughts.getJSONObject(i);
            JSONObject nextThought = llmThoughts.getJSONObject(i + 1);

            var source = currThought.getString("action");
            var target = nextThought.getString("action");
            var event = "TO_" + target;

            transitions
                    .withExternal()
                    .source(source)
                    .target(target)
                    .event(event)
                    .and();
        }

        String lastThought =  llmThoughts.getJSONObject(llmThoughts.length() - 1).getString("action");
        transitions
                .withExternal()
                .source(lastThought)
                .target("END")
                .event("TO_END");
    }

    private Set<String> generateStates() {
        Set<String> states = new HashSet<>();
        for (int i = 0; i < llmThoughts.length(); i++) {
            JSONObject currThought = llmThoughts.getJSONObject(i);
            states.add(currThought.getString("action"));
        }

        return states;
    }
}
