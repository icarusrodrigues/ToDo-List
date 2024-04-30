package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.model.EnumMessage;
import com.personal.project.todolist.response.ResponseHandler;
import com.personal.project.todolist.security.services.UserDetailsImpl;
import com.personal.project.todolist.service.ICrudService;
import com.personal.project.todolist.service.TaskService;
import com.personal.project.todolist.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("tasks")
public class TaskController extends CrudController<TaskDto> {
    @Autowired
    private TaskService service;

    @Autowired
    private UserService userService;

    @Autowired
    public TaskController(ICrudService<TaskDto> service) {
        super(service);
    }

    @Operation(summary = "Get a Task by id", description = "Returns the Task with the provided ID.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Return the Task with the ID provided.",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": {
                                    "id": 0,
                                    "title": "Some Task",
                                    "description": "Some Description",
                                    "dueDate": "2024-03-22T00:00:00"
                                },
                                "message": "Data recovered successfully!",
                                "status": 200
                            }"""))),
            @ApiResponse(responseCode = "404",
                    description = "There is no Task in the database with the provided ID.",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": null,
                                "message": "Entity not found!",
                                "status": 404
                            }""")))
    })
    @Override
    @PreAuthorize("hasRole('PERSONAL') or hasRole('ADMIN') or hasRole('TEAM_MEMBER') or hasRole('ORGANIZATION_MEMBER')")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            var foundTask = service.find(id);
            if (foundTask.getOwnerName().equals(user.getUsername())) {
                return ResponseHandler.generateResponse(super.getById(id), EnumMessage.GET_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CANT_ACCESS_ENTITY_MESSAGE.message());
            }

        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }

    @Operation(summary = "Get all Tasks", description = "Returns a list of all saved Tasks.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Returns a list with all Tasks saved",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": [
                                    {
                                        "id": 0,
                                        "title": "Some Task",
                                        "description": "Some Description",
                                        "dueDate": "2024-03-22T00:00:00"
                                    }
                                ],
                                "message": "Data recovered successfully!",
                                "status": 200
                            }"""))),
            @ApiResponse(responseCode = "400",
                    description = "The passed property does not exist in class Task.",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": null,
                                "message": "Property not found in entity!",
                                "status": 400
                            }"""
                    )))
    })
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "dueDate") String property) {
        try {
            return ResponseHandler.generateResponse(super.list(direction, property), EnumMessage.GET_MESSAGE.message());

        } catch (PropertyReferenceException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message());
        }
    }

    @Operation(summary = "Get all Tasks from the logged in user", description = "Returns a list of all saved Tasks that belong to the logged in user.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Returns a list with all Tasks saved",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": [
                                    {
                                        "id": 0,
                                        "title": "Some Task",
                                        "description": "Some Description",
                                        "dueDate": "2024-03-22T00:00:00"
                                    }
                                ],
                                "message": "Data recovered successfully!",
                                "status": 200
                            }""")))
    })
    @GetMapping("/my-tasks")
    @PreAuthorize("hasRole('PERSONAL') or hasRole('ADMIN') or hasRole('TEAM_MEMBER') or hasRole('ORGANIZATION_MEMBER')")
    public ResponseEntity<?> listTasksByUser() {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseHandler.generateResponse(ResponseEntity.ok().body(service.listAllByOwnerName(user.getUsername())), EnumMessage.GET_MESSAGE.message());
    }

    @Operation(summary = "Create Task", description = "Create a new Task in database.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201",
                    description = "Returns task saved.",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": {
                                    "id": 0,
                                    "title": "Some Task",
                                    "description": "Some Description",
                                    "dueDate": "2024-03-22T00:00:00"
                                },
                                "message": "Data saved successfully!",
                                "status": 201
                            }"""))),
            @ApiResponse(responseCode = "400",
                    description = "Entity not valid",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": null,
                                "message": "Constraint violated!",
                                "status": 400
                            }""")))
    })
    @Override
    @PreAuthorize("hasRole('PERSONAL') or hasRole('ADMIN') or hasRole('TEAM_MEMBER') or hasRole('ORGANIZATION_MEMBER')")
    public ResponseEntity<?> create(@RequestBody @Valid TaskDto dto){
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var foundUser = userService.findByUsername(user.getUsername());

        dto.setOwnerId(foundUser.getId());
        dto.setOwnerName(foundUser.getUsername());

        try {
            return ResponseHandler.generateResponse(super.create(dto), EnumMessage.POST_MESSAGE.message());

        } catch (ConstraintViolationException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());
        }
    }

    @Operation(summary = "Update a Task", description = "Updates a Task that has already been saved.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Returns the updated Task.",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": {
                                    "id": 0,
                                    "title": "Some Task",
                                    "description": "Some Description",
                                    "dueDate": "2024-03-22T00:00:00"
                                },
                                "message": "Data updated successfully!",
                                "status": 200
                            }"""))),
            @ApiResponse(responseCode = "400",
                    description = "Constraint violated!",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": null,
                                "message": "Constraint violated!",
                                "status": 400
                            }"""))),
            @ApiResponse(responseCode = "404",
                    description = "There is no entity in the database with the provided ID.",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": null,
                                "message": "Entity not found!",
                                "status": 404
                            }""")))
    })
    @Override
    @PreAuthorize("hasRole('PERSONAL') or hasRole('ADMIN') or hasRole('TEAM_MEMBER') or hasRole('ORGANIZATION_MEMBER')")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody TaskDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            var foundTask = service.find(id);

            if (foundTask.getOwnerName().equals(user.getUsername())) {
                return ResponseHandler.generateResponse(super.update(id, dto), EnumMessage.PUT_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CANT_ACCESS_ENTITY_MESSAGE.message());
            }

        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());

        } catch (TransactionSystemException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());
        }
    }

    @Operation(summary = "Delete a Task", description = "Delete the entity with the provided ID.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "No content",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "404",
                    description = "There is no entity in the database with the provided ID.",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "data": null,
                                "message": "Entity not found!",
                                "status": 404
                            }""")))
    })
    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasRole('PERSONAL') or hasRole('ADMIN') or hasRole('TEAM_MEMBER') or hasRole('ORGANIZATION_MEMBER')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            var foundTask = service.find(id);

            if (foundTask.getOwnerName().equals(user.getUsername())) {
                return ResponseHandler.generateResponse(super.delete(id), EnumMessage.DELETE_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CANT_ACCESS_ENTITY_MESSAGE.message());
            }
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }
}
