package com.personal.project.todolist.mapper;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.model.User;
import com.personal.project.todolist.model.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends GenericMapper<UserDto, User> {
    @Autowired
    TaskMapper taskMapper;

    @Override
    public UserDto toDto(User entity) {
        UserDto userDto = new UserDto();
        userDto.setId(entity.getId());
        userDto.setUsername(entity.getUsername());
        userDto.setPassword(entity.getPassword());
        userDto.setEmail(entity.getEmail());
        userDto.setUserType(UserType.valueOf(entity.getUserType().name()));
        userDto.setTeam(entity.getTeam());

        if (entity.getTasks() != null)
            userDto.setTasks(taskMapper.toDtoList(entity.getTasks()));

        return userDto;
    }

    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setUserType(UserType.valueOf(dto.getUserType().name()));
        user.setTeam(dto.getTeam());

        if (dto.getTasks() != null)
            user.setTasks(taskMapper.toEntityList(dto.getTasks()));

        return user;
    }
}
