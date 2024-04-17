package com.personal.project.todolist.dto;

import com.personal.project.todolist.model.UserType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto<Long> {

    String username;

    String password;

    String email;

    UserType userType;

    String team;

    List<TaskDto> tasks;
}
