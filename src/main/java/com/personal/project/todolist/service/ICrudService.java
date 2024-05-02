package com.personal.project.todolist.service;

import com.personal.project.todolist.dto.BaseDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ICrudService <T extends BaseDto<Long>>{
    T find(Long id);
    List<T> findAll(Sort.Direction direction, String property);
    T create(T dto);
    T update(Long id, T dto);
    void delete(Long id);
}
