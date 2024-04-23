package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.model.User;
import com.personal.project.todolist.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CrudService<UserDto, User> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(GenericMapper<UserDto, User> mapper, IRepository<User, Long> repository) {
        super(mapper, repository);
    }

    @Override
    public UserDto create(UserDto dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        return super.create(dto);
    }
}
