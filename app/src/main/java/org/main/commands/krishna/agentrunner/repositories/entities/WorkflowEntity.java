package org.main.commands.krishna.agentrunner.repositories.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "workflows", schema = "workflows")
public class WorkflowEntity {
    @Id
    private String id;
    private String userPrompt;
    private String shortTermMemory;
    private String longTermMemory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
