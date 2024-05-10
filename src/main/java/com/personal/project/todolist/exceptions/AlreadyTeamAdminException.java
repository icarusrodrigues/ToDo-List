package com.personal.project.todolist.exceptions;

public class AlreadyTeamAdminException extends Exception {
    public AlreadyTeamAdminException(String errorMessage){
        super(errorMessage);
    }
}
