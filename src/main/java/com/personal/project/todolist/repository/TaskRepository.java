package com.personal.project.todolist.repository;

import com.personal.project.todolist.model.Task;

import java.util.List;

public interface TaskRepository extends IRepository<Task, Long> {

    List<Task> findAllByOwnerName(String ownerName);

}
