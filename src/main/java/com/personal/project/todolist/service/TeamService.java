package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.exceptions.AlreadyTeamAdminException;
import com.personal.project.todolist.exceptions.UserOnTeamException;
import com.personal.project.todolist.exceptions.NotInTeamException;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.mapper.UserMapper;
import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.repository.IRepository;
import com.personal.project.todolist.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService extends CrudService<TeamDto, Team> {

    @Autowired
    private TeamRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    public TeamService(GenericMapper<TeamDto, Team> mapper, IRepository<Team, Long> repository) {
        super(mapper, repository);
    }

    @Override
    public TeamDto update(Long id, TeamDto dto) {
        TeamDto foundTeam = super.find(id);

        if (dto.getName() == null)
            dto.setName(foundTeam.getName());

        if (dto.getTeamLeaderId() == null)
            dto.setTeamLeaderId(foundTeam.getTeamLeaderId());

        if (dto.getAdmins() == null)
            dto.setAdmins(foundTeam.getAdmins());

        if (dto.getMembers() == null)
            dto.setMembers(foundTeam.getMembers());

        if (dto.getTeamTasks() == null)
            dto.setTeamTasks(foundTeam.getTeamTasks());

        return super.update(id, dto);
    }

    public List<TeamDto> listAllLedTeams(UserDto userDto) {
        return repository.findAllByTeamLeader(userMapper.toEntity(userDto))
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<TeamDto> listAllManagedTeams(UserDto userDto) {
        return repository.findByAdminsContaining(userMapper.toEntity(userDto))
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<TeamDto> listAllMemberTeams(UserDto userDto) {
        return repository.findByMembersContaining(userMapper.toEntity(userDto))
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public void addMemberToTeam(Long memberId, TeamDto teamDto) throws UserOnTeamException, AlreadyTeamAdminException {
        var foundMember = userService.find(memberId);

        if (teamDto.getMembers().contains(foundMember)){
            throw new UserOnTeamException("Member already in team!");
        }

        if (teamDto.getAdmins().contains(foundMember)){
            throw new AlreadyTeamAdminException("The User is already an admin of the Team");
        }

        foundMember.getTeams().add(teamDto);
        foundMember.getUserTypes().add(UserType.TEAM_MEMBER);
        userService.update(foundMember.getId(), foundMember);
    }

    public void addAdminToTeam(Long memberId, TeamDto teamDto) throws UserOnTeamException, NotInTeamException {
        var foundAdmin = userService.find(memberId);

        if (teamDto.getAdmins().contains(foundAdmin)){
            throw new UserOnTeamException("Admin already in team!");
        }

        if (teamDto.getMembers().contains(foundAdmin)) {
            foundAdmin.getTeams().remove(teamDto);

            if (foundAdmin.getTeams().isEmpty())
                foundAdmin.getUserTypes().remove(UserType.TEAM_MEMBER);

            foundAdmin.getManagedTeams().add(teamDto);
            foundAdmin.getUserTypes().add(UserType.TEAM_ADMIN);
            userService.update(foundAdmin.getId(), foundAdmin);

        } else {
            throw new NotInTeamException("The new Admin must be a member of the Team '" + teamDto.getName() + "'!");
        }
    }

    public TeamDto changeTeamLeader(Long newLeaderId, UserDto loggedUser, TeamDto teamDto) throws NotInTeamException {
        var foundNewLeader = userService.find(newLeaderId);

        if (teamDto.getMembers().contains(foundNewLeader)){
            foundNewLeader.getUserTypes().add(UserType.TEAM_LEADER);
            foundNewLeader.getTeams().remove(teamDto);

            if (foundNewLeader.getTeams().isEmpty()){
                foundNewLeader.getUserTypes().remove(UserType.TEAM_MEMBER);
            }

        } else if (teamDto.getAdmins().contains(foundNewLeader)) {
            foundNewLeader.getUserTypes().add(UserType.TEAM_LEADER);
            foundNewLeader.getManagedTeams().remove(teamDto);

            if (foundNewLeader.getManagedTeams().isEmpty()){
                foundNewLeader.getUserTypes().remove(UserType.TEAM_ADMIN);
            }

        } else {
            throw new NotInTeamException("The new Leader must be a member of the Team '" + teamDto.getName() + "'!");
        }

        loggedUser.getUserTypes().add(UserType.TEAM_ADMIN);
        loggedUser.getLedTeams().remove(teamDto);
        loggedUser.getManagedTeams().add(teamDto);

        if (loggedUser.getLedTeams().isEmpty()) {
            loggedUser.getUserTypes().remove(UserType.TEAM_LEADER);
        }

        userService.update(foundNewLeader.getId(), foundNewLeader);
        userService.update(loggedUser.getId(), loggedUser);

        teamDto.setTeamLeaderId(foundNewLeader.getId());

        return update(teamDto.getId(), teamDto);
    }

    public void removeTeamsFromUsers(TeamDto teamDto) {
        teamDto.getMembers().forEach(member -> {
            member.getTeams().remove(teamDto);

            if (member.getTeams().isEmpty())
                member.getUserTypes().remove(UserType.TEAM_MEMBER);

            userService.update(member.getId(), member);
        });

        teamDto.getAdmins().forEach(admin -> {
            admin.getManagedTeams().remove(teamDto);

            if (admin.getManagedTeams().isEmpty())
                admin.getUserTypes().remove(UserType.TEAM_ADMIN);

            userService.update(admin.getId(), admin);
        });
    }

    public void expelMemberFromTeam(Long memberId, TeamDto teamDto) throws NotInTeamException {
        var foundMember = userService.find(memberId);

        if (!teamDto.getMembers().contains(foundMember)) {
            throw new NotInTeamException("The User is not a member of the Team '" + teamDto.getName() + "'!");
        }

        foundMember.getTeams().remove(teamDto);

        if (foundMember.getTeams().isEmpty()) {
            foundMember.getUserTypes().remove(UserType.TEAM_MEMBER);
        }

        userService.update(foundMember.getId(), foundMember);
    }

    public void removeAdminRights(Long adminId, TeamDto teamDto) throws NotInTeamException {
        var foundAdmin = userService.find(adminId);

        if (!teamDto.getAdmins().contains(foundAdmin)) {
            throw new NotInTeamException("The User is not an admin of the Team '" + teamDto.getName() + "'!");
        }

        foundAdmin.getManagedTeams().remove(teamDto);
        foundAdmin.getTeams().add(teamDto);
        foundAdmin.getUserTypes().add(UserType.TEAM_MEMBER);

        if (foundAdmin.getManagedTeams().isEmpty()) {
            foundAdmin.getUserTypes().remove(UserType.TEAM_ADMIN);
        }

        userService.update(foundAdmin.getId(), foundAdmin);
    }
}
