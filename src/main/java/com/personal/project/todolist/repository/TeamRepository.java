package com.personal.project.todolist.repository;

import com.personal.project.todolist.model.Team;
import com.personal.project.todolist.model.User;

import java.util.List;

public interface TeamRepository extends IRepository<Team, Long> {

    List<Team> findAllByTeamLeader(User user);

    List<Team> findByAdminsContaining(User user);

    List<Team> findByMembersContaining(User user);

}
