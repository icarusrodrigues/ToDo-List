package com.personal.project.todolist.mapper;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.model.User;
import com.personal.project.todolist.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends GenericMapper<UserDto, User> {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TeamRepository teamRepository;

    @Override
    public UserDto toDto(User entity) {
        return modelMapper.map(entity, UserDto.class);
    }

    @Override
    public User toEntity(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
