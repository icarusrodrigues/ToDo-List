package com.personal.project.todolist.exceptions;

public class NotInTeamException extends Exception{
    public NotInTeamException(String errorMessage){
        super(errorMessage);
    }
}
