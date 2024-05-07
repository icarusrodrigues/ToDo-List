package com.personal.project.todolist.model;

public enum UserType {
    //System administrator
    ADMIN,

    //Personal user, to control your own tasks
    PERSONAL,

    //Team leader, where you have access to all team features
    TEAM_LEADER,

    //Team member, where you can see other team members' tasks and their details
    TEAM_MEMBER,

    //Similar to TEAM MEMBER, but it's necessary to have an organizational e-mail
    ORGANIZATION_MEMBER,
}
