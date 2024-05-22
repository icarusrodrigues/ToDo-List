package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.exceptions.AlreadyTeamAdminException;
import com.personal.project.todolist.exceptions.NotInTeamException;
import com.personal.project.todolist.exceptions.UserOnTeamException;
import com.personal.project.todolist.mapper.TeamMapper;
import com.personal.project.todolist.mapper.UserMapper;
import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@SpringBootTest
public class TeamServiceTest {

    @Autowired
    private TeamService service;

    @Autowired
    private TeamMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private TeamRepository repository;

    @MockBean
    private UserService userService;

    @Test
    void testUpdate() {
        Long id = 1L;
        String name = "Some name";

        var team = new TeamDto();
        team.setId(id);
        team.setName(name);
        team.setTeamLeaderId(id);
        team.setAdmins(List.of());
        team.setMembers(List.of());
        team.setTeamTasks(List.of());

        var newTeam = new TeamDto();
        newTeam.setAdmins(null);
        newTeam.setMembers(null);
        newTeam.setTeamTasks(null);

        when(repository.findById(team.getId())).thenReturn(Optional.ofNullable(mapper.toEntity(team)));
        when(repository.save(mapper.toEntity(team))).thenReturn(mapper.toEntity(team));

        var updatedTeam = service.update(id, newTeam);

        assertEquals(updatedTeam.getId(), id);
        assertEquals(updatedTeam.getName(), name);
        assertEquals(updatedTeam.getTeamLeaderId(), id);
        assertEquals(updatedTeam.getAdmins(), List.of());
        assertEquals(updatedTeam.getMembers(), List.of());
        assertEquals(updatedTeam.getTeamTasks(), List.of());
    }

    @Test
    void shouldListAllLedTeams() {
        Long id = 1L;
        String name = "Some name";

        var leader = new UserDto();

        var team = new Team();
        team.setId(id);
        team.setName(name);

        when(repository.findAllByTeamLeader(userMapper.toEntity(leader))).thenReturn(List.of(team));

        var ledTeams = service.listAllLedTeams(leader);

        assertEquals(ledTeams.get(0).getId(), team.getId());
        assertEquals(ledTeams.get(0).getName(), team.getName());
    }

    @Test
    void shouldListAllManagedTeams() {
        Long id = 1L;
        String name = "Some name";

        var admin = new UserDto();

        var team = new Team();
        team.setId(id);
        team.setName(name);

        when(repository.findByAdminsContaining(userMapper.toEntity(admin))).thenReturn(List.of(team));

        var ledTeams = service.listAllManagedTeams(admin);

        assertEquals(ledTeams.get(0).getId(), team.getId());
        assertEquals(ledTeams.get(0).getName(), team.getName());
    }

    @Test
    void shouldListAllMemberTeams() {
        Long id = 1L;
        String name = "Some name";

        var member = new UserDto();

        var team = new Team();
        team.setId(id);
        team.setName(name);

        when(repository.findByMembersContaining(userMapper.toEntity(member))).thenReturn(List.of(team));

        var ledTeams = service.listAllMemberTeams(member);

        assertEquals(ledTeams.get(0).getId(), team.getId());
        assertEquals(ledTeams.get(0).getName(), team.getName());
    }

    @Test
    void shouldAddMemberToTeam() throws UserOnTeamException, AlreadyTeamAdminException {
        Long id = 1L;
        String name = "Some name";

        var newMember = new UserDto();
        newMember.setId(id);

        var team = new TeamDto();
        team.setId(id);
        team.setName(name);

        var newMemberUpdated = new UserDto();
        newMemberUpdated.setId(1L);
        newMemberUpdated.setTeams(List.of(team));
        newMemberUpdated.setUserTypes(Set.of(UserType.TEAM_MEMBER));

        when(userService.find(id)).thenReturn(newMember);
        when(userService.update(newMember.getId(), newMember)).thenReturn(newMemberUpdated);

        service.addMemberToTeam(newMember.getId(), team);
    }

    @Test
    void addMemberToTeamShouldThrowUserOnTeamException() {
        Long id = 1L;
        String name = "Some name";

        var newMember = new UserDto();
        newMember.setId(id);

        var team = new TeamDto();
        team.setId(id);
        team.setName(name);
        team.setMembers(List.of(newMember));

        when(userService.find(id)).thenReturn(newMember);

        UserOnTeamException exception = assertThrows(UserOnTeamException.class, () -> {
            service.addMemberToTeam(newMember.getId(), team);
        });

        assertEquals("Member already in team!", exception.getMessage());
    }

    @Test
    void addMemberToTeamShouldThrowAlreadyTeamAdminException() {
        Long id = 1L;
        String name = "Some name";

        var newMember = new UserDto();
        newMember.setId(id);

        var team = new TeamDto();
        team.setId(id);
        team.setName(name);
        team.setAdmins(List.of(newMember));

        when(userService.find(id)).thenReturn(newMember);

        AlreadyTeamAdminException exception = assertThrows(AlreadyTeamAdminException.class, () -> service.addMemberToTeam(newMember.getId(), team));

        assertEquals("The User is already an admin of the Team", exception.getMessage());
    }

    @Test
    void shouldAddAdminToTeam() throws UserOnTeamException, NotInTeamException {
        Long id = 1L;
        String name = "Some name";

        var newMember = new UserDto();
        newMember.setId(id);

        var team = new TeamDto();
        team.setId(id);
        team.setName(name);
        team.setMembers(List.of(newMember));

        var newMemberUpdated = new UserDto();
        newMemberUpdated.setId(1L);
        newMemberUpdated.setManagedTeams(List.of(team));
        newMemberUpdated.setUserTypes(Set.of(UserType.TEAM_ADMIN));

        when(userService.find(id)).thenReturn(newMember);
        when(userService.update(newMember.getId(), newMember)).thenReturn(newMemberUpdated);

        service.addAdminToTeam(newMember.getId(), team);
    }

    @Test
    void addAdminToTeamShouldThrowUserOnTeamException() {
        Long id = 1L;
        String name = "Some name";

        var newMember = new UserDto();
        newMember.setId(id);

        var team = new TeamDto();
        team.setId(id);
        team.setName(name);
        team.setAdmins(List.of(newMember));

        when(userService.find(id)).thenReturn(newMember);

        UserOnTeamException exception = assertThrows(UserOnTeamException.class, () -> service.addAdminToTeam(newMember.getId(), team));

        assertEquals("Admin already in team!", exception.getMessage());
    }

    @Test
    void addAdminToTeamShouldThrowNotInTeamException() {
        Long id = 1L;
        String name = "Some name";

        var newMember = new UserDto();
        newMember.setId(id);

        var team = new TeamDto();
        team.setId(id);
        team.setName(name);

        when(userService.find(id)).thenReturn(newMember);

        NotInTeamException exception = assertThrows(NotInTeamException.class, () -> service.addAdminToTeam(newMember.getId(), team));

        assertEquals("The new Admin must be a member of the Team '" + team.getName() + "'!", exception.getMessage());
    }
}
