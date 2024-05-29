package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.exceptions.NotInTeamException;
import com.personal.project.todolist.mapper.TaskMapper;
import com.personal.project.todolist.mapper.TeamMapper;
import com.personal.project.todolist.mapper.UserMapper;
import com.personal.project.todolist.model.Task;
import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.model.User;
import com.personal.project.todolist.repository.TaskRepository;
import com.personal.project.todolist.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {
    @Autowired
    private TaskService service;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeamMapper teamMapper;

    @MockBean
    private TaskRepository repository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private TeamService teamService;

    @Test
    void testFind() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);

        when(repository.findById(id)).thenReturn(Optional.of(task));

        var taskDto = service.find(id);

        assertEquals(taskDto.getId(), id);
        assertEquals(taskDto.getTitle(), title);
        assertEquals(taskDto.getDescription(), description);
        assertEquals(taskDto.getDueDate(), now);
    }

    @Test
    void testFindAll() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);

        when(repository.findAll(Sort.by(Sort.Direction.ASC, "dueDate"))).thenReturn(List.of(task));

        var taskDtoList = service.findAll(Sort.Direction.ASC, "dueDate");

        assertEquals(taskDtoList.get(0).getId(), id);
        assertEquals(taskDtoList.get(0).getTitle(), title);
        assertEquals(taskDtoList.get(0).getDescription(), description);
        assertEquals(taskDtoList.get(0).getDueDate(), now);
    }

    @Test
    void testCreate() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);

        when(repository.save(task)).thenReturn(task);

        var savedTask = service.create(mapper.toDto(task));

        assertEquals(savedTask.getId(), id);
        assertEquals(savedTask.getTitle(), title);
        assertEquals(savedTask.getDescription(), description);
        assertEquals(savedTask.getDueDate(), now);
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);

        var taskDto = new TaskDto();

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);

        var updatedTask = service.update(1L, taskDto);

        assertEquals(updatedTask.getId(), id);
        assertEquals(updatedTask.getTitle(), title);
        assertEquals(updatedTask.getDescription(), description);
        assertEquals(updatedTask.getDueDate(), now);
    }

    @Test
    void testUpdateWhenOwnerIdIsNotNull() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var user = new User();
        user.setId(1L);

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);
        task.setOwner(user);

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setOwnerId(user.getId());

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);
        when(userRepository.findById(taskDto.getOwnerId())).thenReturn(Optional.of(user));

        var updatedTask = service.update(1L, taskDto);

        assertEquals(updatedTask.getId(), id);
        assertEquals(updatedTask.getTitle(), title);
        assertEquals(updatedTask.getDescription(), description);
        assertEquals(updatedTask.getDueDate(), now);
        assertEquals(updatedTask.getOwnerId(), user.getId());
    }

    @Test
    void testDelete() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);
    }

    @Test
    void shouldListAllByOwner() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var owner = new User();
        owner.setId(1L);

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);
        task.setOwner(owner);

        when(repository.findAllByOwner(owner, Sort.by(Sort.Direction.ASC, "dueDate"))).thenReturn(List.of(task));

        var taskList = service.listAllByOwner(userMapper.toDto(owner), Sort.Direction.ASC, "dueDate");

        assertEquals(taskList.get(0).getId(), id);
        assertEquals(taskList.get(0).getTitle(), title);
        assertEquals(taskList.get(0).getDescription(), description);
        assertEquals(taskList.get(0).getDueDate(), now);
        assertEquals(taskList.get(0).getOwnerId(), owner.getId());
    }

    @Test
    void shouldListAllByTeam() {
        Long id = 1L;
        String code = UUID.randomUUID().toString();
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var team = new Team();
        team.setId(1L);

        var task = new Task();
        task.setId(id);
        task.setCode(code);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(now);
        task.setTeam(team);

        when(repository.findAllByTeam(team, Sort.by(Sort.Direction.ASC, "dueDate"))).thenReturn(List.of(task));

        var taskList = service.listAllByTeam(teamMapper.toDto(team), Sort.Direction.ASC, "dueDate");

        assertEquals(taskList.get(0).getId(), id);
        assertEquals(taskList.get(0).getTitle(), title);
        assertEquals(taskList.get(0).getDescription(), description);
        assertEquals(taskList.get(0).getDueDate(), now);
        assertEquals(taskList.get(0).getTeamId(), team.getId());
    }

    @Test
    void shouldChangeTaskOwner() throws NotInTeamException {
        var newOwner = new UserDto();
        newOwner.setId(1L);

        var team = new TeamDto();
        team.setId(1L);
        team.setTeamLeaderId(newOwner.getId());

        var task = new TaskDto();
        task.setId(1L);
        task.setTeamId(team.getId());
        task.setOwnerId(newOwner.getId());

        when(userService.find(newOwner.getId())).thenReturn(newOwner);
        when(teamService.find(task.getTeamId())).thenReturn(team);
        when(repository.findById(task.getId())).thenReturn(Optional.of(mapper.toEntity(task)));
        when(repository.save(mapper.toEntity(task))).thenReturn(mapper.toEntity(task));

        var updatedTask = service.changeTaskOwner(newOwner.getId(), task);

        assertEquals(updatedTask.getId(), 1L);
        assertEquals(updatedTask.getTeamId(), team.getId());
        assertEquals(updatedTask.getOwnerId(), newOwner.getId());
    }

    @Test
    void changeTaskOwnerShouldThrowANotInTeamException() {
        var newOwner = new UserDto();
        newOwner.setId(1L);

        var team = new TeamDto();
        team.setId(1L);
        team.setName("Some name");
        team.setTeamLeaderId(2L);
        team.setAdmins(List.of());
        team.setMembers(List.of());

        var task = new TaskDto();
        task.setId(1L);
        task.setTeamId(team.getId());
        task.setOwnerId(newOwner.getId());

        when(userService.find(newOwner.getId())).thenReturn(newOwner);
        when(teamService.find(task.getTeamId())).thenReturn(team);

        NotInTeamException exception = assertThrows(NotInTeamException.class, () -> service.changeTaskOwner(newOwner.getId(), task));

        assertEquals("The new Owner must be a member of the Team '" + team.getName() + "'!", exception.getMessage());
    }
}
