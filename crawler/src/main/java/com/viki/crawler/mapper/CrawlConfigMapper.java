package com.viki.crawler.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Viki on 2017/6/14.
 */
@Mapper
public interface CrawlConfigMapper {

    @Select("select cc.* from t_crawl_config cc where cc.last_update < date_add(now(), interval - cc.period second)")
    public List<HashMap<String,Object>> getList();

    @Update("update t_crawl_config set last_update = now() where url_reg = #{m.url_reg}")
    public int update(@Param("m") HashMap<String,Object> params);
}
