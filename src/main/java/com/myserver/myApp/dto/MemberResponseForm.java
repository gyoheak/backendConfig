package com.myserver.myApp.dto;

import com.myserver.myApp.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseForm {
    private String email;

    public static MemberResponseForm of(Member member){
        return new MemberResponseForm(member.getEmail());
    }
}
