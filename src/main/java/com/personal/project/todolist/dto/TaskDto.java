package com.personal.project.todolist.dto;

import com.personal.project.todolist.model.TaskType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskDto extends BaseDto<Long>{
    String title;
    String description;
    LocalDateTime dueDate;
    TaskType taskType;
    Long createdById;
    Long ownerId;
    Long teamId;
}
