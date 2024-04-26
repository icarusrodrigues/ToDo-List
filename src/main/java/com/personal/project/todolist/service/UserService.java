package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.model.User;
import com.personal.project.todolist.repository.IRepository;
import com.personal.project.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CrudService<UserDto, User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserService(GenericMapper<UserDto, User> mapper, IRepository<User, Long> repository) {
        super(mapper, repository);
    }

    public UserDto findByUsername(String username){
        return userRepository.findByUsername(username).map(mapper::toDto).orElseThrow();
    }

    public User getUserEntityFindByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
