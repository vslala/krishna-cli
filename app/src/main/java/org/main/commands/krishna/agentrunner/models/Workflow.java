package org.main.commands.krishna.agentrunner.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.main.commands.krishna.agentrunner.repositories.entities.WorkflowEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Workflow {
    private String id;
    private String userPrompt;
    @Builder.Default
    private String shortTermMemory = "{}";

    @Builder.Default
    private String longTermMemory = "[]";

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("UTC"));

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(ZoneId.of("UTC"));

    public static Workflow fromEntity(WorkflowEntity workflowEntity) {
        return Workflow.builder()
                .id(workflowEntity.getId())
                .userPrompt(workflowEntity.getUserPrompt())
                .shortTermMemory(workflowEntity.getShortTermMemory() == null ? "{}" : workflowEntity.getShortTermMemory())
                .longTermMemory(workflowEntity.getLongTermMemory() == null ? "[]" : workflowEntity.getLongTermMemory())
                .createdAt(workflowEntity.getCreatedAt())
                .updatedAt(workflowEntity.getUpdatedAt())
                .build();
    }

    public JSONArray getLongTermMemory() {
        return new JSONArray(longTermMemory);
    }

    public ShortTermMemory getShortTermMemory() {
        JSONObject memory = new JSONObject(this.shortTermMemory);
        return ShortTermMemory.builder()
                .previousState(memory.optString("previousState"))
                .previousOutput(memory.optString("previousOutput"))
                .currentState(memory.optString("currentState"))
                .currentOutput(memory.optString("currentOutput"))
                .build();
    }

    public Workflow updateShortTermMemory(ShortTermMemory currentMemory) {
        ShortTermMemory oldMemory = getShortTermMemory();
        JSONObject recentShortTermMemory = new JSONObject(
                ShortTermMemory.builder()
                        .previousState(oldMemory.getCurrentState())
                        .previousOutput(oldMemory.getCurrentOutput())
                        .currentState(currentMemory.getCurrentState())
                        .currentOutput(currentMemory.getCurrentOutput())
                        .build()
        );
        return this.toBuilder()
                .shortTermMemory(recentShortTermMemory.toString())
                .longTermMemory(new JSONArray(this.longTermMemory).put(recentShortTermMemory).toString())
                .updatedAt(LocalDateTime.now(ZoneId.of("UTC")))
                .build();

    }

    public WorkflowEntity toEntity() {
        var entity = new WorkflowEntity();
        entity.setId(this.id);
        entity.setShortTermMemory(this.shortTermMemory);
        entity.setLongTermMemory(this.longTermMemory);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);

        return entity;
    }
}
