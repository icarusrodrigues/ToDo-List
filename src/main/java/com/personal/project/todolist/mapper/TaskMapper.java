package com.personal.project.todolist.mapper;

import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.model.Task;
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
