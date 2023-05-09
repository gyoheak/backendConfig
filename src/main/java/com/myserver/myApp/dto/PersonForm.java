package com.myserver.myApp.dto;

import java.util.Date;

import com.myserver.myApp.entity.Person;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class PersonForm {
    private Long id;
    private String name;
    private String age;
    private Date date;

    public Person toEntity() {
        return new Person(id, name, Integer.parseInt(age), date);
    }
}
