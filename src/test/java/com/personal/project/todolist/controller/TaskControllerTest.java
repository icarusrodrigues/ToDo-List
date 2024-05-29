package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.exceptions.NotInTeamException;
import com.personal.project.todolist.mapper.UserMapper;
import com.personal.project.todolist.model.*;
import com.personal.project.todolist.security.jwt.JwtUtils;
import com.personal.project.todolist.security.services.UserDetailsImpl;
import com.personal.project.todolist.security.services.UserDetailsServiceImpl;
import com.personal.project.todolist.service.TaskService;
import com.personal.project.todolist.service.TeamService;
import com.personal.project.todolist.service.UserService;
import io.restassured.RestAssured;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDateTime;
import java.util.*;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {
    @MockBean
    private TaskService service;

    @MockBean
    private UserService userService;

    @MockBean
    private TeamService teamService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtils jwtUtils;

    private User getLoggedUser() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("some@email");
        user.setUserTypes(Set.of(UserType.values()));

        return user;
    }

    private UserDetailsImpl getLoggedUserDetails() {
        return UserDetailsImpl.build(getLoggedUser());
    }

    private Map<String, Object> getHeaderMap() {
        var loggedUser = getLoggedUserDetails();
        Authentication authentication = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword(), loggedUser.getAuthorities());

        var token = jwtUtils.generateJwtToken(authentication);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        when(userDetailsService.loadUserByUsername(loggedUser.getUsername())).thenReturn(getLoggedUserDetails());
        when(userService.findByUsername(loggedUser.getUsername())).thenReturn(userMapper.toDto(getLoggedUser()));

        return headers;
    }

    @Test
    void getByIdShouldReturnOKResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.find(1L)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .get("/tasks/1")
                .then().log().all()
                .statusCode(200)
                .body("data.id", equalTo(taskDto.getId().intValue()))
                .body("data.title", equalTo(taskDto.getTitle()))
                .body("data.description", equalTo(taskDto.getDescription()))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()))
                .body("status", equalTo(200));
    }

    @Test
    void getByIdShouldReturnCantAccessResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setOwnerId(2L);

        when(service.find(1L)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .get("/tasks/1")
                .then().log().all()
                .statusCode(400)
                .body("data.id", equalTo(null))
                .body("message", equalTo(EnumMessage.CANT_ACCESS_ENTITY_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void getByIdShouldReturnNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
                .headers(getHeaderMap())
                .contentType(JSON)
                .get("/tasks/1")
                .then().log().all()
                .statusCode(404)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(404));
    }

    @Test
    void listShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);

        when(service.findAll(Sort.Direction.ASC, "dueDate")).thenReturn(List.of(taskDto));

        given().log().all()
                .when()
                .contentType(JSON)
                .get("/tasks?direction=ASC&property=dueDate")
                .then().log().all()
                .statusCode(200)
                .body("data[0].id", equalTo(taskDto.getId().intValue()))
                .body("data[0].title", equalTo(taskDto.getTitle()))
                .body("data[0].description", equalTo(taskDto.getDescription()))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()))
                .body("status", equalTo(200));
    }

    @Test
    void listShouldReturnPropertyNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new PropertyReferenceException("name", TypeInformation.OBJECT, List.of())).when(service).findAll(Sort.Direction.ASC, "name");

        given().log().all()
                .when()
                .contentType(JSON)
                .get("/tasks?direction=ASC&property=name")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void listTaskByUserShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.listAllByOwner(userMapper.toDto(getLoggedUser()), Sort.Direction.ASC, "dueDate")).thenReturn(List.of(taskDto));
        when(userService.findByUsername(getLoggedUser().getUsername())).thenReturn(userMapper.toDto(getLoggedUser()));

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .get("/tasks/my-tasks")
                .then().log().all()
                .statusCode(200)
                .body("data", equalTo(List.of()))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()))
                .body("status", equalTo(200));
    }

    @Test
    void listTaskByTeamShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);
        team.setTeamLeaderId(getLoggedUser().getId());

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.TEAM_TASK);
        taskDto.setTeamId(team.getId());
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.listAllByTeam(team, Sort.Direction.ASC, "dueDate")).thenReturn(List.of(taskDto));
        when(teamService.find(team.getId())).thenReturn(team);
        when(userService.findByUsername(getLoggedUser().getUsername())).thenReturn(userMapper.toDto(getLoggedUser()));

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .get("/tasks/team/1")
                .then().log().all()
                .statusCode(200)
                .body("data[0].id", equalTo(taskDto.getId().intValue()))
                .body("data[0].title", equalTo(taskDto.getTitle()))
                .body("data[0].description", equalTo(taskDto.getDescription()))
                .body("data[0].taskType", equalTo(taskDto.getTaskType().name()))
                .body("data[0].ownerId", equalTo(taskDto.getOwnerId().intValue()))
                .body("data[0].teamId", equalTo(taskDto.getTeamId().intValue()))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()))
                .body("status", equalTo(200));
    }

    @Test
    void listTaskByTeamShouldReturnDontHavePermissionResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);
        team.setTeamLeaderId(2L);

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.TEAM_TASK);
        taskDto.setTeamId(team.getId());
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.listAllByTeam(team, Sort.Direction.ASC, "dueDate")).thenReturn(List.of(taskDto));
        when(teamService.find(team.getId())).thenReturn(team);
        when(userService.findByUsername(getLoggedUser().getUsername())).thenReturn(userMapper.toDto(getLoggedUser()));

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .get("/tasks/team/1")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void listTaskByTeamShouldReturnPropertyNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);
        team.setTeamLeaderId(getLoggedUser().getId());

        doThrow(new PropertyReferenceException("name", TypeInformation.OBJECT, List.of())).when(service).listAllByTeam(team, Sort.Direction.ASC, "name");
        when(teamService.find(team.getId())).thenReturn(team);
        when(userService.findByUsername(getLoggedUser().getUsername())).thenReturn(userMapper.toDto(getLoggedUser()));

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .param("direction", "ASC")
                .param("property", "name")
                .get("/tasks/team/1")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void createShouldReturnCreatedResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.PERSONAL_TASK);
        taskDto.setCreatedById(getLoggedUser().getId());
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.create(taskDto)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .post("/tasks")
                .then().log().all()
                .statusCode(201)
                .body("data.id", equalTo(taskDto.getId().intValue()))
                .body("data.title", equalTo(taskDto.getTitle()))
                .body("data.description", equalTo(taskDto.getDescription()))
                .body("data.taskType", equalTo(taskDto.getTaskType().name()))
                .body("data.createdById", equalTo(taskDto.getCreatedById().intValue()))
                .body("data.ownerId", equalTo(taskDto.getOwnerId().intValue()))
                .body("data.teamId", equalTo(null))
                .body("message", equalTo(EnumMessage.POST_MESSAGE.message()))
                .body("status", equalTo(201));
    }

    @Test
    void createShouldReturnConstraintViolationResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var taskDto = new TaskDto();
        taskDto.setTaskType(TaskType.PERSONAL_TASK);
        taskDto.setCreatedById(getLoggedUser().getId());
        taskDto.setOwnerId(getLoggedUser().getId());

        doThrow(new ConstraintViolationException(Set.of())).when(service).create(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .post("/tasks")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void createTeamTaskShouldReturnCreatedResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);
        team.setTeamLeaderId(getLoggedUser().getId());

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.TEAM_TASK);
        taskDto.setCreatedById(getLoggedUser().getId());
        taskDto.setOwnerId(getLoggedUser().getId());
        taskDto.setTeamId(team.getId());

        when(service.create(taskDto)).thenReturn(taskDto);
        when(teamService.find(team.getId())).thenReturn(team);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .post("/tasks/team/1")
                .then().log().all()
                .statusCode(201)
                .body("data.id", equalTo(taskDto.getId().intValue()))
                .body("data.title", equalTo(taskDto.getTitle()))
                .body("data.description", equalTo(taskDto.getDescription()))
                .body("data.taskType", equalTo(taskDto.getTaskType().name()))
                .body("data.createdById", equalTo(taskDto.getCreatedById().intValue()))
                .body("data.ownerId", equalTo(taskDto.getOwnerId().intValue()))
                .body("data.teamId", equalTo(taskDto.getTeamId().intValue()))
                .body("message", equalTo(EnumMessage.POST_MESSAGE.message()))
                .body("status", equalTo(201));
    }

    @Test
    void createTeamTaskShouldReturnDontHavePermissionResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);
        team.setTeamLeaderId(2L);

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.TEAM_TASK);
        taskDto.setCreatedById(getLoggedUser().getId());
        taskDto.setOwnerId(getLoggedUser().getId());
        taskDto.setTeamId(team.getId());

        when(service.create(taskDto)).thenReturn(taskDto);
        when(teamService.find(team.getId())).thenReturn(team);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .post("/tasks/team/1")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void createTeamTaskShouldReturnEntityNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);

        doThrow(new NoSuchElementException()).when(teamService).find(team.getId());

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(new TaskDto())
                .post("/tasks/team/1")
                .then().log().all()
                .statusCode(404)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(404));
    }

    @Test
    void updateShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.PERSONAL_TASK);
        taskDto.setCreatedById(getLoggedUser().getId());
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.find(1L)).thenReturn(taskDto);
        when(service.update(1L, taskDto)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .put("/tasks/1")
                .then().log().all()
                .statusCode(200)
                .body("data.id", equalTo(taskDto.getId().intValue()))
                .body("data.title", equalTo(taskDto.getTitle()))
                .body("data.description", equalTo(taskDto.getDescription()))
                .body("message", equalTo(EnumMessage.PUT_MESSAGE.message()))
                .body("status", equalTo(200));
    }

    @Test
    void updateShouldReturnCantAccessResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setOwnerId(2L);

        when(service.find(1L)).thenReturn(taskDto);
        when(service.update(1L, taskDto)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .put("/tasks/1")
                .then().log().all()
                .statusCode(400)
                .body("data.id", equalTo(null))
                .body("message", equalTo(EnumMessage.CANT_ACCESS_ENTITY_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void updateShouldReturnNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(new TaskDto())
                .put("/tasks/1")
                .then().log().all()
                .statusCode(404)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(404));
    }

    @Test
    void updateShouldReturnConstraintViolationResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var taskDto = new TaskDto();
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.find(1L)).thenReturn(taskDto);
        doThrow(new TransactionSystemException("")).when(service).update(1L, taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .put("/tasks/1")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void changeTaskOwnerShouldReturnUpdatedResponse() throws NotInTeamException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.TEAM_TASK);
        taskDto.setTeamId(team.getId());
        taskDto.setOwnerId(getLoggedUser().getId());

        var updatedTaskDto = new TaskDto();
        updatedTaskDto.setId(id);
        updatedTaskDto.setTitle(title);
        updatedTaskDto.setDescription(description);
        updatedTaskDto.setDueDate(now);
        updatedTaskDto.setTaskType(TaskType.TEAM_TASK);
        updatedTaskDto.setTeamId(team.getId());
        updatedTaskDto.setOwnerId(2L);

        when(service.find(taskDto.getId())).thenReturn(taskDto);
        when(service.changeTaskOwner(2L, taskDto)).thenReturn(updatedTaskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .put("/tasks/1/new-owner/2")
                .then().log().all()
                .statusCode(200)
                .body("data.id", equalTo(updatedTaskDto.getId().intValue()))
                .body("data.title", equalTo(updatedTaskDto.getTitle()))
                .body("data.description", equalTo(updatedTaskDto.getDescription()))
                .body("data.taskType", equalTo(updatedTaskDto.getTaskType().name()))
                .body("data.ownerId", equalTo(updatedTaskDto.getOwnerId().intValue()))
                .body("data.teamId", equalTo(updatedTaskDto.getTeamId().intValue()))
                .body("message", equalTo(EnumMessage.PUT_MESSAGE.message()))
                .body("status", equalTo(200));
    }

    @Test
    void changeTaskOwnerShouldReturnCantAccessResponse() throws NotInTeamException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.TEAM_TASK);
        taskDto.setTeamId(team.getId());
        taskDto.setOwnerId(2L);

        when(service.find(taskDto.getId())).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .put("/tasks/1/new-owner/2")
                .then().log().all()
                .statusCode(400)
                .body("data.id", equalTo(null))
                .body("message", equalTo(EnumMessage.CANT_ACCESS_ENTITY_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void changeTaskOwnerShouldReturnNotATeamTaskResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.PERSONAL_TASK);
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.find(taskDto.getId())).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .put("/tasks/1/new-owner/2")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo("This is not a Team Task!"))
                .body("status", equalTo(400));
    }

    @Test
    void changeTaskOwnerShouldReturnEntityNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(new TaskDto())
                .put("/tasks/1/new-owner/2")
                .then().log().all()
                .statusCode(404)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(404));
    }

    @Test
    void changeTaskOwnerShouldReturnNotInTeamResponse() throws NotInTeamException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var team = new TeamDto();
        team.setId(1L);

        Long id = 1L;
        String title = "some title";
        String description = "some description";
        LocalDateTime now = LocalDateTime.now();

        var taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setDueDate(now);
        taskDto.setTaskType(TaskType.TEAM_TASK);
        taskDto.setTeamId(team.getId());
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.find(taskDto.getId())).thenReturn(taskDto);
        doThrow(new NotInTeamException("The new Owner must be a member of the Team!")).when(service).changeTaskOwner(2L, taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .body(taskDto)
                .put("/tasks/1/new-owner/2")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo("The new Owner must be a member of the Team!"))
                .body("status", equalTo(400));
    }

    @Test
    void deleteShouldReturnNoContentResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var taskDto = new TaskDto();
        taskDto.setOwnerId(getLoggedUser().getId());

        when(service.find(1L)).thenReturn(taskDto);
        doNothing().when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .delete("/tasks/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void deleteShouldReturnCantAccessResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var taskDto = new TaskDto();
        taskDto.setOwnerId(2L);

        when(service.find(1L)).thenReturn(taskDto);
        doNothing().when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .delete("/tasks/1")
                .then().log().all()
                .statusCode(400);
    }


    @Test
    void deleteShouldReturnNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
                .contentType(JSON)
                .headers(getHeaderMap())
                .delete("/tasks/1")
                .then().log().all()
                .statusCode(404)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(404));
    }
}