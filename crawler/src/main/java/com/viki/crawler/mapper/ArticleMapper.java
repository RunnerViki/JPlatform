package com.viki.crawler.mapper;

import com.viki.crawler.article.ArticleDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Viki on 2017/6/14.
 */
@Mapper
public interface ArticleMapper {

    @Insert("insert into t_article(title, content, post_date, url) " +
            "values(" +
            "#{a.title},#{a.content},#{a.post_date},#{a.url}" +
            ") on duplicate key update post_date = values(post_date)")
    public int insertArticle(@Param("a") ArticleDTO article);
}
