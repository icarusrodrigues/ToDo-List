package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.dto.auth.LoginRequestDto;
import com.personal.project.todolist.dto.auth.RegisterRequestDto;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.response.ResponseHandler;
import com.personal.project.todolist.security.jwt.JwtUtils;
import com.personal.project.todolist.security.services.UserDetailsImpl;
import com.personal.project.todolist.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginDto) {
        try {
            var foundUser = userService.findByUsername(loginDto.getUsername());

            if (passwordEncoder.matches(loginDto.getPassword(), foundUser.getPassword())){
                var userDetails = UserDetailsImpl.build(userService.getUserEntityFindByUsername(foundUser.getUsername()));

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                var token = jwtUtils.generateJwtToken(authentication);

                var responseMap = new HashMap<String, String>();
                responseMap.put("username", foundUser.getUsername());
                responseMap.put("email", foundUser.getEmail());
                responseMap.put("userTypes", foundUser.getUserTypes().toString());
                responseMap.put("token", token);

                return ResponseEntity.ok(responseMap);
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User or password doesn't match!");
            }

        } catch (NoSuchElementException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User or password doesn't match!");
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerDto) {
        try {
            UserDto userDto = new UserDto();

            userDto.setUsername(registerDto.getUsername());
            userDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            userDto.setEmail(registerDto.getEmail());
            userDto.getUserTypes().add(UserType.PERSONAL);

            return ResponseEntity.ok(userService.create(userDto));

        } catch (DataIntegrityViolationException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "Username already in use!");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build();
    }
}
