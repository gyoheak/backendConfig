package com.myserver.myApp.repository;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.myserver.myApp.entity.Article;

public interface ArticleRepository extends CrudRepository<Article, Long> {
    @Override
    ArrayList<Article> findAll();
}
