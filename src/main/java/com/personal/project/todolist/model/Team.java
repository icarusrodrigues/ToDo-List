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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_leader_id", referencedColumnName = "id")
    private User teamLeader;

    @ManyToMany(mappedBy = "managedTeams")
    private List<User> admins;

    @ManyToMany(mappedBy = "teams")
    private List<User> members;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private List<Task> teamTasks;
}

