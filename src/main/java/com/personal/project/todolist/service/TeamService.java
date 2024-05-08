package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.dto.UserDto;
import com.personal.project.todolist.exceptions.MemberOnTeamException;
import com.personal.project.todolist.exceptions.NotTeamMemberException;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.model.UserType;
import com.personal.project.todolist.repository.IRepository;
import com.personal.project.todolist.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class TeamService extends CrudService<TeamDto, Team> {

    @Autowired
    private TeamRepository repository;

    @Autowired
    private UserService userService;

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

        if (dto.getMembers() == null)
            dto.setMembers(foundTeam.getMembers());

        return super.update(id, dto);
    }

    public void addMemberToTeam(Long memberId, TeamDto teamDto) throws MemberOnTeamException {
        var foundMember = userService.find(memberId);

        if (teamDto.getMembers().contains(foundMember)){
            throw new MemberOnTeamException("Member already in team!");
        }

        foundMember.getTeams().add(teamDto);
        foundMember.getUserTypes().add(UserType.TEAM_MEMBER);
        userService.update(foundMember.getId(), foundMember);
    }

    public TeamDto changeTeamLeader(Long newLeaderId, UserDto loggedUser, TeamDto teamDto) throws NotTeamMemberException {
        var foundNewLeader = userService.find(newLeaderId);

        if (!teamDto.getMembers().contains(foundNewLeader)) {
            throw new NotTeamMemberException("The new Leader must be a member of the Team '" + teamDto.getName() + "'!");
        }

        foundNewLeader.getUserTypes().add(UserType.TEAM_LEADER);
        loggedUser.getUserTypes().add(UserType.TEAM_MEMBER);

        if (!isMember(foundNewLeader, teamDto.getId())){
            foundNewLeader.getUserTypes().remove(UserType.TEAM_MEMBER);
        }

        if (!isLeader(loggedUser, teamDto.getId())) {
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

            if (member.getId().equals(teamDto.getTeamLeaderId())) {
                if (!isLeader(member, teamDto.getId()))
                    member.getUserTypes().remove(UserType.TEAM_LEADER);
            }else {
                if (member.getTeams().isEmpty())
                    member.getUserTypes().remove(UserType.TEAM_MEMBER);
            }

            userService.update(member.getId(), member);
        });
    }

    public void expelMemberFromTeam(Long memberId, TeamDto teamDto) throws NotTeamMemberException {
        var foundMember = userService.find(memberId);

        if (!teamDto.getMembers().contains(foundMember)) {
            throw new NotTeamMemberException("The User is not a member of the Team '" + teamDto.getName() + "'!");
        }

        foundMember.getTeams().remove(teamDto);

        if (foundMember.getTeams().isEmpty()) {
            foundMember.getUserTypes().remove(UserType.TEAM_MEMBER);
        }

        userService.update(foundMember.getId(), foundMember);
    }

    private Boolean isMember(UserDto userDto, Long teamId) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);

        userDto.getTeams().forEach(team -> {
            if (!team.getTeamLeaderId().equals(userDto.getId()) && !team.getId().equals(teamId))
                result.set(true);
        });

        return result.get();
    }

    private Boolean isLeader(UserDto userDto, Long teamId) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);

        userDto.getTeams().forEach(team -> {
            if (team.getTeamLeaderId().equals(userDto.getId()) && !team.getId().equals(teamId))
                result.set(true);
        });

        return result.get();
    }
}
