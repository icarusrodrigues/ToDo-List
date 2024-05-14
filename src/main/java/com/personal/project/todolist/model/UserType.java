package com.personal.project.todolist.model;

public enum UserType {
    //System administrator
    ADMIN,

    //Personal user, to control your own tasks
    PERSONAL,

    //Team member, where you can see other team members' tasks and their details
    TEAM_MEMBER,

    //Team administrator, where you have access to all team features, except expel other admins and deleting the team
    TEAM_ADMIN,

    //Team leader, where you have access to all team features
    TEAM_LEADER
}
