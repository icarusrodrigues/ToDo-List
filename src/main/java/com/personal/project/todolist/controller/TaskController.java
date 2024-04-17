package com.challenge.nuven.todolist.controller;

import com.challenge.nuven.todolist.dto.TaskDto;
import com.challenge.nuven.todolist.model.EnumMessage;
import com.challenge.nuven.todolist.response.ResponseHandler;
import com.challenge.nuven.todolist.service.ICrudService;
import com.challenge.nuven.todolist.service.TaskService;
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
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("tasks")
public class TaskController extends CrudController<TaskDto> {
    @Autowired
    private TaskService service;

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
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try {
            return ResponseHandler.generateResponse(super.getById(id), EnumMessage.GET_MESSAGE.message());

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
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "dueDate") String property) {
        try {
            return ResponseHandler.generateResponse(super.list(direction, property), EnumMessage.GET_MESSAGE.message());

        } catch (PropertyReferenceException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message());
        }
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
    public ResponseEntity<?> create(@RequestBody @Valid TaskDto dto){
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
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody TaskDto dto) {
        try {
            return ResponseHandler.generateResponse(super.update(id, dto), EnumMessage.PUT_MESSAGE.message());

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
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            return ResponseHandler.generateResponse(super.delete(id), EnumMessage.DELETE_MESSAGE.message());

        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }
}
