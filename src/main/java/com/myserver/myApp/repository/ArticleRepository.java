package com.myserver.myApp.repository;

import org.springframework.data.repository.CrudRepository;

import com.myserver.myApp.entity.Article;

public interface ArticleRepository extends CrudRepository<Article, Long> {

}
