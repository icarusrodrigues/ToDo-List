package com.personal.project.todolist.repository;

import com.personal.project.todolist.model.Task;
import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.model.User;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface TaskRepository extends IRepository<Task, Long> {

    List<Task> findAllByOwner(User owner, Sort sort);

    List<Task> findAllByTeam(Team team, Sort sort);
}
