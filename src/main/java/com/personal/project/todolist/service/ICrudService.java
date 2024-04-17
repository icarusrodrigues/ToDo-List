package com.challenge.nuven.todolist.service;

import com.challenge.nuven.todolist.dto.BaseDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

//@NoRepositoryBean
public interface ICrudService <T extends BaseDto<Long>>{
    T find(Long id);
    List<T> findAll(Sort.Direction direction, String property);
    T create(T dto);
    T update(Long id, T dto);
    void delete(Long id);
}
