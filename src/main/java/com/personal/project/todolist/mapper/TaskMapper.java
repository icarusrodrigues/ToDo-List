package com.personal.project.todolist.mapper;

import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.model.Task;
import com.personal.project.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskMapper extends GenericMapper<TaskDto, Task>{

    @Autowired
    private UserRepository userRepository;

    public TaskDto toDto(Task entity) {
        TaskDto dto = new TaskDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setDueDate(entity.getDueDate());
        dto.setOwnerId(entity.getOwner().getId());
        dto.setOwnerName(entity.getOwnerName());

        return dto;
    }

    @Override
    public Task toEntity(TaskDto dto) {
        Task entity = new Task();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setDueDate(dto.getDueDate());
        entity.setOwner(userRepository.findById(dto.getOwnerId()).get());
        entity.setOwnerName(dto.getOwnerName());

        return entity;
    }

    public List<TaskDto> toDtoList(List<Task> entities){
        List<TaskDto> dtos = new ArrayList<>();

        entities.forEach(task -> {
            dtos.add(this.toDto(task));
        });

        return dtos;
    }

    public List<Task> toEntityList(List<TaskDto> dtos){
        List<Task> entities = new ArrayList<>();

        dtos.forEach(task -> {
            entities.add(this.toEntity(task));
        });

        return entities;
    }
}
