package com.myserver.myApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Article
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor // 디폴트 생성자 추가
@ToString
@Getter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB가 id 자동생성
    private Long id;

    @Column
    private String title;

    @Column
    private String content;
}