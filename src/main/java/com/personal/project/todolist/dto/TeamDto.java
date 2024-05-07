package com.personal.project.todolist.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TeamDto extends BaseDto<Long> {
    String name;

    Long teamLeaderId;

    @JsonIgnore
    List<UserDto> members = new ArrayList<>();
}
