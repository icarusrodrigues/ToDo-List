package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.service.ICrudService;
import com.personal.project.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController extends CrudController<UserDto> {
    @Autowired
    private UserService service;

    @Autowired
    public UserController(ICrudService<UserDto> service) {
        super(service);
    }

}
