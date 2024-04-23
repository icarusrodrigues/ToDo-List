package com.personal.project.todolist.dto.auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
