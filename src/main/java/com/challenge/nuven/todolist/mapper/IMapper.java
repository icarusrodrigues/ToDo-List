package com.challenge.nuven.todolist.mapper;

public interface IMapper <T, E>{
    T toDto(E entity);
    E toEntity(T dto);
}
