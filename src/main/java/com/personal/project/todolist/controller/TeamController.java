package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.model.EnumMessage;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.response.ResponseHandler;
import com.personal.project.todolist.security.services.UserDetailsImpl;
import com.personal.project.todolist.service.ICrudService;
import com.personal.project.todolist.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("teams")
public class TeamController extends CrudController<TeamDto> {

    @Autowired
    private UserService userService;

    public TeamController(ICrudService<TeamDto> service) {
        super(service);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEAM_LEADER', 'TEAM_MEMBER')")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(id);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId()) || foundTeam.getMembers().contains(loggedUser)) {
                return ResponseHandler.generateResponse(super.getById(id), EnumMessage.GET_MESSAGE.message());

            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());

            }
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());

        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "id") String property) {
        try {
            return ResponseHandler.generateResponse(super.list(direction, property), EnumMessage.GET_MESSAGE.message());

        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message());

        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'ADMIN', 'TEAM_LEADER', 'TEAM_MEMBER', 'ORGANIZATION_MEMBER')")
    public ResponseEntity<?> create(@RequestBody TeamDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            dto.setTeamLeaderId(loggedUser.getId());
            var savedTeam = service.create(dto);

            loggedUser.getTeams().add(savedTeam);
            loggedUser.getUserTypes().add(UserType.TEAM_LEADER);
            userService.update(loggedUser.getId(), loggedUser);

            return ResponseHandler.generateResponse(ResponseEntity.ok(savedTeam), EnumMessage.POST_MESSAGE.message());
        }
        catch (ConstraintViolationException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEAM_LEADER')")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody TeamDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(id);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())) {
                return ResponseHandler.generateResponse(super.update(id, dto), EnumMessage.PUT_MESSAGE.message());

            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());

            }

        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());

        } catch (TransactionSystemException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());

        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEAM_LEADER')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(id);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())) {
                return ResponseHandler.generateResponse(super.delete(id), EnumMessage.DELETE_MESSAGE.message());

            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());

            }
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());

        }
    }

    @PostMapping("/add/{teamId}/{memberId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEAM_LEADER')")
    public ResponseEntity<?> addMemberToTeam(@PathVariable(name = "teamId") Long teamId,
                                             @PathVariable(name = "memberId") Long memberId) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(teamId);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())){
                try {
                    var foundMember = userService.find(memberId);

                    foundMember.getTeams().add(foundTeam);
                    foundMember.getUserTypes().add(UserType.TEAM_MEMBER);
                    userService.update(foundMember.getId(), foundMember);

                    return ResponseEntity.ok().build();
                } catch (NoSuchElementException e){
                    return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
                }

            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());

            }

        } catch (NoSuchElementException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());

        }
    }
}
