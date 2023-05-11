package com.myserver.myApp.repositoryImpl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myserver.myApp.entity.Article;
import com.myserver.myApp.entity.QArticle;
import com.myserver.myApp.repository.ArticleRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {
    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public ArrayList<Article> findArticleByAuthor(String author) {
        return (ArrayList<Article>) queryFactory
                .selectFrom(QArticle.article)
                .where(QArticle.article.author.eq(author))
                .fetch();
    }
}
