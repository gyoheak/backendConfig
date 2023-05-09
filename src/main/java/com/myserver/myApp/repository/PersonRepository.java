package com.myserver.myApp.repository;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.myserver.myApp.entity.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {
    @Override
    public ArrayList<Person> findAll();
}
