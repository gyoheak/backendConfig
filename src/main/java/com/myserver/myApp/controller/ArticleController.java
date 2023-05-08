package com.myserver.myApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.myserver.myApp.dto.ArticleForm;
import com.myserver.myApp.entity.Article;
import com.myserver.myApp.repository.ArticleRepository;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j // lombok internal logger
public class ArticleController {

    @Autowired // 스프링 부트가 미리 생성해 둔 객체를 가져다가 자동 연결
    private ArticleRepository articleRepository;

    @GetMapping("/article/new")
    public String newArticle() {
        return "article/new";
    }

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

    @GetMapping("articles/{id}")
    public String showArticle(@PathVariable Long id, Model model) {
        // 1.id로 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);
        // 2.가져온 데이터를 Model에 담기
        log.info(articleEntity.toString());
        model.addAttribute("article", articleEntity);
        // 3.화면에 보여주기 view에 전달
        return "article/show";
    }

    @GetMapping("/articles")
    public String index(Model model) {
        // 1. 모든 데이터 가져오기
        List<Article> articleEntityList = articleRepository.findAll();
        // 2.가져온 Article 묶음을 뷰로 전달
        model.addAttribute("articleList", articleEntityList);
        // 3.뷰페이지 설정

        return "articles/index";
    }
}