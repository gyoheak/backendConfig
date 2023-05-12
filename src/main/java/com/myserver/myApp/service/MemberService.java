package com.myserver.myApp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myserver.myApp.dto.MemberResponseForm;
import com.myserver.myApp.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberResponseForm findMemberById(Long id) {
        return memberRepository.findById(id)
                .map(MemberResponseForm::of)
                .orElseThrow(() -> new RuntimeException("해당 회원이 없습니다. id=" + id));
    }

    public MemberResponseForm findMemberInfoByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponseForm::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다."));
    }
}
