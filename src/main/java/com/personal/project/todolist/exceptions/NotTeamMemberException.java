package com.personal.project.todolist.exceptions;

public class NotTeamMemberException extends Exception{
    public NotTeamMemberException(String errorMessage){
        super(errorMessage);
    }
}
