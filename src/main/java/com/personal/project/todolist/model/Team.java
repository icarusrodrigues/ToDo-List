package com.personal.project.todolist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "teams")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Team extends BaseEntity<Long> {
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_leader_id", referencedColumnName = "id")
    private User teamLeader;

    @ManyToMany(mappedBy = "teams")
    private List<User> members;
}

