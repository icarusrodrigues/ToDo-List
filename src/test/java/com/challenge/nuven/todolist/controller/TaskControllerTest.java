package com.challenge.nuven.todolist.controller;

import com.challenge.nuven.todolist.dto.TaskDto;
import com.challenge.nuven.todolist.model.EnumMessage;
import com.challenge.nuven.todolist.model.Task;
import com.challenge.nuven.todolist.service.TaskService;
import io.restassured.RestAssured;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {
    @MockBean
    private TaskService service;

    @LocalServerPort
    private int port;

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

        when(service.find(1L)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
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
    void getByIdShouldReturnNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
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

        when(service.create(taskDto)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .body(taskDto)
                .post("/tasks")
                .then().log().all()
                .statusCode(201)
                .body("data.id", equalTo(taskDto.getId().intValue()))
                .body("data.title", equalTo(taskDto.getTitle()))
                .body("data.description", equalTo(taskDto.getDescription()))
                .body("message", equalTo(EnumMessage.POST_MESSAGE.message()))
                .body("status", equalTo(201));
    }

    @Test
    void createShouldReturnConstraintViolationResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var taskDto = new TaskDto();

        doThrow(new ConstraintViolationException(Set.of())).when(service).create(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
                .body(taskDto)
                .post("/tasks")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()))
                .body("status", equalTo(400));
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

        when(service.update(1L, taskDto)).thenReturn(taskDto);

        given().log().all()
                .when()
                .contentType(JSON)
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
    void updateShouldReturnNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).update(1L, new TaskDto());

        given().log().all()
                .when()
                .contentType(JSON)
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

        doThrow(new TransactionSystemException("")).when(service).update(1L, new TaskDto());

        given().log().all()
                .when()
                .contentType(JSON)
                .body(new TaskDto())
                .put("/tasks/1")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()))
                .body("status", equalTo(400));
    }

    @Test
    void deleteShouldReturnNoContentResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doNothing().when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(JSON)
                .delete("/tasks/1")
                .then().log().all()
                .statusCode(204);
    }


    @Test
    void deleteShouldReturnNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(JSON)
                .delete("/tasks/1")
                .then().log().all()
                .statusCode(404)
                .body("data", equalTo(null))
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()))
                .body("status", equalTo(404));
    }

}