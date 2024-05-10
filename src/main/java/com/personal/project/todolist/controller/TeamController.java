package com.personal.project.todolist.controller;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.exceptions.AlreadyTeamAdminException;
import com.personal.project.todolist.exceptions.UserOnTeamException;
import com.personal.project.todolist.exceptions.NotInTeamException;
import com.personal.project.todolist.model.EnumMessage;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.response.ResponseHandler;
import com.personal.project.todolist.security.services.UserDetailsImpl;
import com.personal.project.todolist.service.ICrudService;
import com.personal.project.todolist.service.TeamService;
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
    private TeamService service;

    @Autowired
    private UserService userService;

    public TeamController(ICrudService<TeamDto> service) {
        super(service);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEAM_LEADER', 'TEAM_ADMIN', 'TEAM_MEMBER')")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(id);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId()) || foundTeam.getMembers().contains(loggedUser) || loggedUser.getUserTypes().contains(UserType.ADMIN)) {
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

    @GetMapping("/my-teams/leader")
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'ADMIN', 'TEAM_LEADER', 'TEAM_ADMIN', 'TEAM_MEMBER')")
    public ResponseEntity<?> listLedTeamsByUser() {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        return ResponseHandler.generateResponse(ResponseEntity.ok(service.listAllLedTeams(loggedUser)), EnumMessage.GET_MESSAGE.message());
    }

    @GetMapping("/my-teams/admin")
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'ADMIN', 'TEAM_LEADER', 'TEAM_ADMIN', 'TEAM_MEMBER')")
    public ResponseEntity<?> listManagedTeamsByUser() {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        return ResponseHandler.generateResponse(ResponseEntity.ok(service.listAllManagedTeams(loggedUser)), EnumMessage.GET_MESSAGE.message());
    }

    @GetMapping("/my-teams/member")
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'ADMIN', 'TEAM_LEADER', 'TEAM_ADMIN', 'TEAM_MEMBER')")
    public ResponseEntity<?> listMemberTeamsByUser() {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        return ResponseHandler.generateResponse(ResponseEntity.ok(service.listAllMemberTeams(loggedUser)), EnumMessage.GET_MESSAGE.message());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'ADMIN', 'TEAM_LEADER', 'TEAM_ADMIN', 'TEAM_MEMBER')")
    public ResponseEntity<?> create(@RequestBody TeamDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            dto.setTeamLeaderId(loggedUser.getId());
            var savedTeam = service.create(dto);

            loggedUser.getUserTypes().add(UserType.TEAM_LEADER);
            userService.update(loggedUser.getId(), loggedUser);

            return ResponseHandler.generateResponse(ResponseEntity.ok(savedTeam), EnumMessage.POST_MESSAGE.message());
        }
        catch (ConstraintViolationException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('TEAM_ADMIN','TEAM_LEADER')")
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
    @PreAuthorize("hasAnyAuthority('TEAM_LEADER')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(id);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())) {
                service.removeTeamsFromUsers(foundTeam);

                return ResponseHandler.generateResponse(super.delete(id), EnumMessage.DELETE_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
            }
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }

    @PostMapping("/add/member/{teamId}/{memberId}")
    @PreAuthorize("hasAnyAuthority('TEAM_ADMIN','TEAM_LEADER')")
    public ResponseEntity<?> addMember(@PathVariable(name = "teamId") Long teamId,
                                       @PathVariable(name = "memberId") Long memberId) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(teamId);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())){
                service.addMemberToTeam(memberId, foundTeam);

                return ResponseHandler.generateResponse(ResponseEntity.ok().build(), EnumMessage.PUT_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
            }
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (UserOnTeamException | AlreadyTeamAdminException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), e.getMessage());
        }
    }
    @PostMapping("/add/admin/{teamId}/{adminId}")
    @PreAuthorize("hasAnyAuthority('TEAM_LEADER')")
    public ResponseEntity<?> addAdmin(@PathVariable(name = "teamId") Long teamId,
                                       @PathVariable(name = "adminId") Long adminId) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(teamId);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())){
                service.addAdminToTeam(adminId, foundTeam);

                return ResponseHandler.generateResponse(ResponseEntity.ok().build(), EnumMessage.PUT_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
            }
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (UserOnTeamException | NotInTeamException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), e.getMessage());
        }
    }

    @PutMapping("/expel/admin/{teamId}/{adminId}")
    @PreAuthorize("hasAnyAuthority('TEAM_LEADER')")
    public ResponseEntity<?> removeAdmin(@PathVariable(name = "teamId") Long teamId,
                                         @PathVariable(name = "adminId") Long adminId) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(teamId);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())){
                service.removeAdminRights(adminId, foundTeam);

                return ResponseHandler.generateResponse(ResponseEntity.ok().build(), EnumMessage.PUT_MESSAGE.message());

            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
            }
        } catch (NotInTeamException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), e.getMessage());
        }
    }

    @PutMapping("/change-leader/{teamId}/{newLeaderId}")
    @PreAuthorize("hasAnyAuthority('TEAM_ADMIN','TEAM_LEADER')")
    public ResponseEntity<?> changeLeader(@PathVariable(name = "teamId") Long teamId,
                                              @PathVariable(name = "newLeaderId") Long newLeaderId) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(teamId);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())) {
                return ResponseHandler.generateResponse(ResponseEntity.ok(service.changeTeamLeader(newLeaderId, loggedUser, foundTeam)), EnumMessage.PUT_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
            }
        } catch (NoSuchElementException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (NotInTeamException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), e.getMessage());
        }
    }

    @PutMapping("/expel/member/{teamId}/{memberId}")
    @PreAuthorize("hasAnyAuthority('TEAM_ADMIN','TEAM_LEADER')")
    public ResponseEntity<?> expelMember(@PathVariable(name = "teamId") Long teamId,
                                         @PathVariable(name = "memberId") Long memberId) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundTeam = service.find(teamId);

            if (foundTeam.getTeamLeaderId().equals(loggedUser.getId())) {
                service.expelMemberFromTeam(memberId, foundTeam);

                return ResponseHandler.generateResponse(ResponseEntity.noContent().build(), EnumMessage.PUT_MESSAGE.message());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
            }

        } catch (NoSuchElementException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (NotInTeamException e) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), e.getMessage());
        }
    }
}
