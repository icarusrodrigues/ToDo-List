package com.personal.project.todolist.dto.auth;

import com.personal.project.todolist.model.UserType;
import lombok.Data;

@Data
public class RegisterRequestDto {
    String username;
    String password;
    String email;
    UserType userType;
    String team;
}
