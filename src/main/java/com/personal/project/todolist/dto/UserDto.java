package com.personal.project.todolist.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personal.project.todolist.model.UserType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class UserDto extends BaseDto<Long> {

    String username;

    @JsonIgnore
    String password;

    String email;

    Set<UserType> userTypes = new HashSet<>();

    @EqualsAndHashCode.Exclude
    List<TeamDto> teams = new ArrayList<>();

    List<TaskDto> tasks;
}
