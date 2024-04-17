package com.personal.project.todolist.service;


import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.model.Task;
import com.personal.project.todolist.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService extends CrudService<TaskDto, Task> {

    @Autowired
    public TaskService(GenericMapper<TaskDto, Task> mapper, IRepository<Task, Long> repository) {
        super(mapper, repository);
    }

    @Override
    public TaskDto create(TaskDto dto) {
        if (dto.getDueDate() == null) {
            dto.setDueDate(LocalDateTime.now().plusWeeks(1));
        }

        return super.create(dto);
    }

    @Override
    public TaskDto update(Long id, TaskDto dto) {
        TaskDto foundTask = super.find(id);
        if (dto.getTitle() == null)
            dto.setTitle(foundTask.getTitle());

        if (dto.getDescription() == null)
            dto.setDescription(foundTask.getDescription());

        if (dto.getDueDate() == null)
            dto.setDueDate(foundTask.getDueDate());

        return super.update(id, dto);
    }
}
