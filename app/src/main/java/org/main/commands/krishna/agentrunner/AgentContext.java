package org.main.commands.krishna.agentrunner;

import org.json.JSONArray;
import org.main.commands.krishna.agentrunner.models.ShortTermMemory;
import org.main.commands.krishna.agentrunner.models.Workflow;
import org.main.commands.krishna.agentrunner.repositories.WorkflowRepository;
import org.main.commands.krishna.agentrunner.repositories.entities.WorkflowEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class AgentContext {
    private final WorkflowRepository workflowRepository;

    @Autowired
    public AgentContext(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    public void save(String workFlowId, ShortTermMemory shortTermMemory) {
        Optional<WorkflowEntity> currWorkflow = this.workflowRepository.findById(workFlowId);
        if (currWorkflow.isPresent()) {
            var workflow = Workflow.fromEntity(currWorkflow.get());
            var workFlowEntity = workflow.updateShortTermMemory(shortTermMemory).toEntity();
            this.workflowRepository.save(workFlowEntity);
        } else {
            var newWorkflow = Workflow.builder()
                    .id(workFlowId)
                    .shortTermMemory(shortTermMemory.toJson().toString())
                    .longTermMemory(new JSONArray().put(shortTermMemory.toJson()).toString())
                    .createdAt(LocalDateTime.now(ZoneId.of("UTC")))
                    .updatedAt(LocalDateTime.now(ZoneId.of("UTC")))
                    .build();
            this.workflowRepository.save(newWorkflow.toEntity());
        }
    }

    public Workflow get(String workflowId) {
        return Workflow.fromEntity(
                this.workflowRepository.findById(workflowId)
                        .orElseThrow()
        );
    }
}
