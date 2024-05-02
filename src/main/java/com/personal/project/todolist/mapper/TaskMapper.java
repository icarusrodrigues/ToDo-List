package com.personal.project.todolist.mapper;

import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.model.Task;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper extends GenericMapper<TaskDto, Task>{
    @Autowired
    ModelMapper modelMapper;

    public TaskDto toDto(Task entity) {
        return modelMapper.map(entity, TaskDto.class);
    }

    @Override
    public Task toEntity(TaskDto dto) {
        return modelMapper.map(dto, Task.class);
    }

}
