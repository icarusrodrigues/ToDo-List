package com.personal.project.todolist.dto;

import com.personal.project.todolist.model.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserDtoTest {
    @Test
    void testNoArgsConstructor() {
        var id = 1L;
        var username = "some username";
        var password = "password";
        var email = "some@email.com";
        var userTypes = Set.of(UserType.ADMIN);
        var listTeams = new ArrayList<TeamDto>();
        var listTasks = new ArrayList<TaskDto>();

        var dto = new UserDto();
        dto.setId(id);
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setEmail(email);
        dto.setUserTypes(userTypes);
        dto.setLedTeams(listTeams);
        dto.setManagedTeams(listTeams);
        dto.setTeams(listTeams);
        dto.setCreatedTasks(listTasks);
        dto.setOwnedTasks(listTasks);

        assertEquals(dto.getId(), id);
        assertEquals(dto.getUsername(), username);
        assertEquals(dto.getPassword(), password);
        assertEquals(dto.getEmail(), email);
        assertEquals(dto.getUserTypes(), userTypes);
        assertEquals(dto.getLedTeams(), listTeams);
        assertEquals(dto.getManagedTeams(), listTeams);
        assertEquals(dto.getTeams(), listTeams);
        assertEquals(dto.getCreatedTasks(), listTasks);
        assertEquals(dto.getOwnedTasks(), listTasks);
    }

    @Test
    void testEquals() {
        var dto1 = UserDto.builder().build();
        var dto2 = UserDto.builder().build();

        assertEquals(dto1, dto2);
    }

    @Test
    void testEqualsAndHashCode() {
        var dto1 = new UserDto();
        var dto2 = new UserDto();

        assertEquals(dto1, dto2);
    }

    @Test
    void testToString() {
        var toString = "UserDto()";

        var dto = new UserDto();

        assertEquals(dto.toString(), toString);
    }
}

