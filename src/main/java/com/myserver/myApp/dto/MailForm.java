package com.myserver.myApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class MailForm {
    private String to;
    private String subject;
    private String text;
}
