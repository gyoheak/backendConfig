package com.myserver.myApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myserver.myApp.dto.ArticleForm;
import com.myserver.myApp.entity.Article;
import com.myserver.myApp.repository.ArticleRepository;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j //lombok internal logger
public class ArticleController {

    @Autowired // 스프링 부트가 미리 생성해 둔 객체를 가져다가 자동 연결
    private ArticleRepository articleRepository;

    // @GetMapping("/article/new")
    // public String newArticle() {
    //     return "article/new";
    // } 
    
    @PostMapping("/article/create")
    public String createArticle(ArticleForm form) {
        // DTO 변환
        Article article = form.toEntity();
        log.info(article.toString());
        // DB 저장
        Article saved = articleRepository.save(article);
        log.info(saved.toString());
        return "article/new";
    }
}
