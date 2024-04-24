package com.personal.project.todolist.security.services;

import com.personal.project.todolist.model.User;
import com.personal.project.todolist.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository
                .findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username:" + username));

        return UserDetailsImpl.build(foundUser);
    }
}
