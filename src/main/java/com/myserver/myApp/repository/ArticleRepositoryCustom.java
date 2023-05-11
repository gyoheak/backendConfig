package com.myserver.myApp.repository;

import java.util.ArrayList;

import com.myserver.myApp.entity.Article;

public interface ArticleRepositoryCustom {
    ArrayList<Article> findArticleByAuthor(String author);
}
