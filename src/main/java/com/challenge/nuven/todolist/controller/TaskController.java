package com.challenge.nuven.todolist.controller;

import com.challenge.nuven.todolist.dto.TaskDto;
import com.challenge.nuven.todolist.model.EnumMessage;
import com.challenge.nuven.todolist.response.ResponseHandler;
import com.challenge.nuven.todolist.service.ICrudService;
import com.challenge.nuven.todolist.service.TaskService;
import jakarta.validation.ConstraintViolationException;
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

    @Override
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try {
            return ResponseHandler.generateResponse(super.getById(id), EnumMessage.GET_MESSAGE.message());

        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }

    @Override
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "dueDate") String property) {
        try {
            return ResponseHandler.generateResponse(super.list(direction, property), EnumMessage.GET_MESSAGE.message());

        } catch (PropertyReferenceException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message());
        }
    }

    @Override
    public ResponseEntity<?> create(@RequestBody TaskDto dto){
        try {
            return ResponseHandler.generateResponse(super.create(dto), EnumMessage.POST_MESSAGE.message());

        } catch (ConstraintViolationException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());
        }
    }

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

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            return ResponseHandler.generateResponse(super.delete(id), EnumMessage.DELETE_MESSAGE.message());

        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }
}
