package com.personal.project.todolist.mapper;

import com.personal.project.todolist.dto.TeamDto;
import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TeamMapper extends GenericMapper<TeamDto, Team> {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    public TeamDto toDto(Team entity) {
        return modelMapper.map(entity, TeamDto.class);
    }

    @Override
    public Team toEntity(TeamDto dto) {
        return modelMapper.map(dto, Team.class);
    }
}
