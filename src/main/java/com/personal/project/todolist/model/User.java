package com.personal.project.todolist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity<Long>{

    @NotBlank
    private String username;

    @NotBlank
    @JsonIgnore
    private String password;

    @NotBlank
    private String email;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String team;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private List<Task> tasks;
}
