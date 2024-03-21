package com.challenge.nuven.todolist.repository;

import com.challenge.nuven.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends IRepository<Task, Long> {
}
