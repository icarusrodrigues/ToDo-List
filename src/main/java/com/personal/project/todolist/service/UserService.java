package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.model.User;
import com.personal.project.todolist.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CrudService<UserDto, User> {

    @Autowired
    public UserService(GenericMapper<UserDto, User> mapper, IRepository<User, Long> repository) {
        super(mapper, repository);
    }

}
