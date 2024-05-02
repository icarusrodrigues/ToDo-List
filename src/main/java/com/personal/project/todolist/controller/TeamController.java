package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.security.services.UserDetailsImpl;
import com.personal.project.todolist.service.ICrudService;
import com.personal.project.todolist.service.TeamService;
import com.personal.project.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("teams")
public class TeamController extends CrudController<TeamDto> {
    @Autowired
    private TeamService service;

    @Autowired
    private UserService userService;

    public TeamController(ICrudService<TeamDto> service) {
        super(service);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'ADMIN', 'TEAM_MEMBER', 'ORGANIZATION_MEMBER')")
    public ResponseEntity<?> create(@RequestBody TeamDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var foundUser = userService.findByUsername(user.getUsername());
        var savedTeam = service.create(dto);

        foundUser.getTeams().add(savedTeam);
        userService.update(foundUser.getId(), foundUser);

        return ResponseEntity.ok(savedTeam);
    }
}
