package com.personal.project.todolist.repository;

import com.personal.project.todolist.model.User;

import java.util.Optional;

public interface UserRepository extends IRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
