package com.personal.project.todolist.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TeamDtoTest {
    @Test
    void testNoArgsConstructor() {
        var id = 1L;
        var name = "Some name";
        var leaderId = 1L;
        var listUser = new ArrayList<UserDto>();
        var listTask = new ArrayList<TaskDto>();

        var dto = new TeamDto();
        dto.setId(id);
        dto.setName(name);
        dto.setTeamLeaderId(leaderId);
        dto.setAdmins(listUser);
        dto.setMembers(listUser);
        dto.setTeamTasks(listTask);

        assertEquals(dto.getId(), id);
        assertEquals(dto.getName(), name);
        assertEquals(dto.getTeamLeaderId(), leaderId);
        assertEquals(dto.getAdmins(), listUser);
        assertEquals(dto.getMembers(), listUser);
        assertEquals(dto.getTeamTasks(), listTask);

    }

    @Test
    void testEquals() {
        var dto1 = TeamDto.builder().build();
        var dto2 = TeamDto.builder().build();

        assertEquals(dto1, dto2);
    }

    @Test
    void testEqualsAndHashCode() {
        var dto1 = new TeamDto();
        var dto2 = new TeamDto();

        assertEquals(dto1, dto2);
    }
}
