package com.myserver.myApp.controller;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myserver.myApp.dto.PersonForm;
import com.myserver.myApp.entity.Person;
import com.myserver.myApp.repository.PersonRepository;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PersonController {
    @Autowired
    PersonRepository personRepository;

    @GetMapping("/personList")
    public String personList() {
        ArrayList<Person> personList = personRepository.findAll();
        personList.forEach((p) -> {
            log.info(p.getName().toString());
        });
        return personList.toString();
    }

    @PostMapping("/person/create")
    public String createPerson(PersonForm form) {
        Person personEntity = form.toEntity();
        personEntity.setDate(new Date());
        Person person = personRepository.save(personEntity);
        log.info(person.toString());
        return "redirect:/person/new";
    }
}
