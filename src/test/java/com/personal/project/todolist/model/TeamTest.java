package com.personal.project.todolist.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TeamTest {

    @Test
    void testAllArgsConstructor() {
        var name = "Some name";
        var leader = new User();
        var listUser = new ArrayList<User>();
        var listTask = new ArrayList<Task>();

        var team = new Team(name, leader, listUser, listUser, listTask);

        assertEquals(team.getName(), name);
        assertEquals(team.getTeamLeader(), leader);
        assertEquals(team.getAdmins(), listUser);
        assertEquals(team.getMembers(), listUser);
        assertEquals(team.getTeamTasks(), listTask);
    }

    @Test
    void testEquals() {
        var team1 = new Team();
        var team2 = new Team();

        assertEquals(team1, team2);
    }

}
