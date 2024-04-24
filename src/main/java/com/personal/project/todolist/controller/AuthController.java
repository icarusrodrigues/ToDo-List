package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.dto.auth.LoginRequestDto;
import com.personal.project.todolist.dto.auth.RegisterRequestDto;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.response.ResponseHandler;
import com.personal.project.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto) {
        try {
            var foundUser = userService.findByUsername(loginDto.getUsername());

            if (passwordEncoder.matches(loginDto.getPassword(), foundUser.getPassword())){
                return ResponseEntity.ok(loginDto);
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User or password doesn't match!");
            }

        } catch (NoSuchElementException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User or password doesn't match!");
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerDto) {
        UserDto userDto = new UserDto();

        userDto.setUsername(registerDto.getUsername());
        userDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userDto.setEmail(registerDto.getEmail());
        userDto.setUserType(UserType.valueOf(registerDto.getUserType().name()));
        userDto.setTeam(registerDto.getTeam());
        userDto.setTasks(new ArrayList<>());

        return ResponseEntity.ok(userService.create(userDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build();
    }
}
