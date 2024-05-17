package com.personal.project.todolist.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserTest {

    @Test
    void testAllArgsConstructor() {
        var username = "Some username";
        var password = "password";
        var email = "some@email.com";
        var userTypes = Set.of(UserType.ADMIN);
        var listTeams = new ArrayList<Team>();
        var listTasks = new ArrayList<Task>();

        var user = new User(username, password, email, userTypes, listTeams, listTeams, listTeams, listTasks, listTasks);

        assertEquals(user.getUsername(), username);
        assertEquals(user.getPassword(), password);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getUserTypes(), userTypes);
        assertEquals(user.getLedTeams(), listTeams);
        assertEquals(user.getManagedTeams(), listTeams);
        assertEquals(user.getTeams(), listTeams);
        assertEquals(user.getCreatedTasks(), listTasks);
        assertEquals(user.getOwnedTasks(), listTasks);
    }

    @Test
    void testEquals() {
        var user1 = new User();
        var user2 = new User();

        assertEquals(user1, user2);
    }
}
