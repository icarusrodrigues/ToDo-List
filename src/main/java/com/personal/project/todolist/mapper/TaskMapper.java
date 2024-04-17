package com.challenge.nuven.todolist.mapper;

import com.challenge.nuven.todolist.dto.TaskDto;
import com.challenge.nuven.todolist.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper extends GenericMapper<TaskDto, Task>{
    public TaskDto toDto(Task entity) {
        TaskDto dto = new TaskDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setDueDate(entity.getDueDate());

        return dto;
    }

    @Override
    public Task toEntity(TaskDto dto) {
        Task entity = new Task();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setDueDate(dto.getDueDate());

        return entity;    }
}
