package com.challenge.nuven.todolist.service;

import com.challenge.nuven.todolist.dto.TaskDto;
import com.challenge.nuven.todolist.mapper.GenericMapper;
import com.challenge.nuven.todolist.model.Task;
import com.challenge.nuven.todolist.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
}
