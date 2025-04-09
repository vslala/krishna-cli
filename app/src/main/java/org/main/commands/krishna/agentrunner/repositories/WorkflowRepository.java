package org.main.commands.krishna.agentrunner.repositories;

import org.main.commands.krishna.agentrunner.repositories.entities.WorkflowEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends CrudRepository<WorkflowEntity, String> {
}
