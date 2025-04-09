package org.main.commands.krishna.agentrunner;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgentStateMachineTest {

    private void waitForState(StateMachine<String, String> sm, String expectedState) throws InterruptedException {
        int retries = 10;
        while (retries-- > 0 && !sm.getState().getId().equals(expectedState)) {
            Thread.sleep(50); // small wait to let the transition finish
        }
        assertEquals(expectedState, sm.getState().getId());
    }


    @Test
    void should_generate_dynamic_states_based_on_the_llm_thoughts() throws Exception {
        String llmThoughts = """
                [
                  { "thought": "I need to search for the stock price", "action": "WEB_SEARCH", "input": "Amazon share price" },
                  { "thought": "Now I should parse the result", "action": "PARSE_RESULT" },
                  { "thought": "Finally, I will respond to the user", "action": "RESPOND" }
                ]
                """;

        var agentStateMachine = new AgentStateMachine();
        StateMachine<String, String> stateMachine = agentStateMachine.build(llmThoughts);

        stateMachine.startReactively().block();

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload("TO_WEB_SEARCH").build())).blockLast();
        waitForState(stateMachine, "WEB_SEARCH");

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload("TO_PARSE_RESULT").build())).blockLast();
        waitForState(stateMachine, "PARSE_RESULT");

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload("TO_RESPOND").build())).blockLast();
        waitForState(stateMachine, "RESPOND");

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload("TO_END").build())).blockLast();
        waitForState(stateMachine, "END");
    }

    @Test
    void should_transition_to_the_correct_state() {

    }

}
