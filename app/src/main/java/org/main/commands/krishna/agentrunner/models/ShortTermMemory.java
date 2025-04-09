package org.main.commands.krishna.agentrunner.models;

import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;

@Data
@Builder(toBuilder = true)
public class ShortTermMemory {
    private String userPrompt;
    private String chainOfThoughts;
    private String previousState;
    private String previousOutput;
    private String currentState;
    private String currentOutput;

    public JSONObject toJson() {
        return new JSONObject(this);
    }
}
