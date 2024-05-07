package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.mapper.GenericMapper;
import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.repository.IRepository;
import com.personal.project.todolist.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService extends CrudService<TeamDto, Team> {

    @Autowired
    private TeamRepository repository;

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
}
