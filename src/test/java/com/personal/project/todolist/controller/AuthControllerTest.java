package com.personal.project.todolist.controller;


import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.dto.auth.LoginRequestDto;
import com.personal.project.todolist.dto.auth.RegisterRequestDto;
import com.personal.project.todolist.mapper.UserMapper;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.repository.UserRepository;
import com.personal.project.todolist.security.jwt.JwtUtils;
import com.personal.project.todolist.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @LocalServerPort
    private int port;

    @Test
    void loginShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String password = "Some password";
        String email = "some@email";

        var login = new LoginRequestDto();
        login.setUsername(username);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));
        savedUser.setEmail(email);
        savedUser.setUserTypes(Set.of(UserType.ADMIN));

        when(userService.findByUsername(login.getUsername())).thenReturn(savedUser);
        when(userService.getUserEntityFindByUsername(login.getUsername())).thenReturn(userMapper.toEntity(savedUser));

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(200)
                .body("email", equalTo(savedUser.getEmail()))
                .body("username", equalTo(savedUser.getUsername()));
    }

    @Test
    void loginShouldReturnBadRequestWhenPasswordDontMatch() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String password = "Some password";

        var login = new LoginRequestDto();
        login.setUsername(username);
        login.setPassword(password + 1);

        var savedUser = new UserDto();
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        when(userService.findByUsername(login.getUsername())).thenReturn(savedUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo("User or password doesn't match!"))
                .body("status", equalTo(400));
    }

    @Test
    void loginShouldReturnBadRequestWhenUserDontExist() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String password = "Some password";

        var login = new LoginRequestDto();
        login.setUsername(username);
        login.setPassword(password);

        doThrow(new NoSuchElementException()).when(userService).findByUsername(login.getUsername());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(400)
                .body("data", equalTo(null))
                .body("message", equalTo("User or password doesn't match!"))
                .body("status", equalTo(400));;
    }

    @Test
    void signUpShouldReturnOk() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String password = "Some password";
        String email = "some@email";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setPassword(password);
        register.setEmail(email);

        var createdUser = new UserDto();
        createdUser.setUsername(username);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setEmail(email);
        createdUser.setUserTypes(Set.of(UserType.PERSONAL));

        when(userService.create(createdUser)).thenReturn(createdUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(200 );
    }

    @Test
    void logoutShouldReturnNoContent() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .post("auth/logout")
                .then().log().all()
                .statusCode(204);
    }
}
