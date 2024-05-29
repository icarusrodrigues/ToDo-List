package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.TaskDto;
import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.exceptions.NotInTeamException;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.mapper.TeamMapper;
import com.personal.project.todolist.mapper.UserMapper;
import com.personal.project.todolist.model.Task;
import com.personal.project.todolist.repository.IRepository;
import com.personal.project.todolist.repository.TaskRepository;
import com.personal.project.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService extends CrudService<TaskDto, Task> {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    public TaskService(GenericMapper<TaskDto, Task> mapper, IRepository<Task, Long> repository) {
        super(mapper, repository);
    }

    @Override
    public TaskDto create(TaskDto dto) {
        if (dto.getDueDate() == null) {
            dto.setDueDate(LocalDateTime.now().plusWeeks(1));
        }

        return super.create(dto);
    }

    @Override
    public TaskDto update(Long id, TaskDto dto) {
        TaskDto foundTask = super.find(id);
        if (dto.getTitle() == null)
            dto.setTitle(foundTask.getTitle());

        if (dto.getDescription() == null)
            dto.setDescription(foundTask.getDescription());

        if (dto.getDueDate() == null)
            dto.setDueDate(foundTask.getDueDate());

        if (dto.getOwnerId() == null) {
            dto.setOwnerId(foundTask.getOwnerId());

        } else {
            var foundUser = userRepository.findById(dto.getOwnerId());

            foundUser.ifPresent(user -> dto.setOwnerId(user.getId()));
        }

        return super.update(id, dto);
    }

    public List<TaskDto> listAllByOwner(UserDto owner, Sort.Direction direction, String property) {
        return taskRepository.findAllByOwner(userMapper.toEntity(owner), Sort.by(direction, property)).stream().map(mapper::toDto).toList();
    }

    public List<TaskDto> listAllByTeam(TeamDto teamDto, Sort.Direction direction, String property) {
        return taskRepository.findAllByTeam(teamMapper.toEntity(teamDto), Sort.by(direction, property)).stream().map(mapper::toDto).toList();
    }

    public TaskDto changeTaskOwner(Long newOwnerId, TaskDto taskDto) throws NotInTeamException {
        var newOwner = userService.find(newOwnerId);
        var taskTeam = teamService.find(taskDto.getTeamId());

        if (!taskTeam.getTeamLeaderId().equals(newOwnerId) && !taskTeam.getAdmins().contains(newOwner) && !taskTeam.getMembers().contains(newOwner)) {
            throw new NotInTeamException("The new Owner must be a member of the Team '" + taskTeam.getName() + "'!");
        }

        taskDto.setOwnerId(newOwnerId);
        return super.update(taskDto.getId(), taskDto);
    }
}
