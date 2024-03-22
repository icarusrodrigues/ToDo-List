package com.challenge.nuven.todolist.service;

import com.challenge.nuven.todolist.dto.TaskDto;
import com.challenge.nuven.todolist.mapper.TaskMapper;
import com.challenge.nuven.todolist.model.Task;
import com.challenge.nuven.todolist.repository.TaskRepository;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TaskServiceTest {
    @Autowired
    private TaskService service;

    @Autowired
    private TaskMapper mapper;

    @MockBean
    private TaskRepository repository;

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
}
