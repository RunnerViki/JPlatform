package com.viki.crawlConfig.mapper;

import com.viki.crawlConfig.bean.WebsiteConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Viki on 2017/6/4.
 */
@Mapper
public interface WebsiteCompletedMapper {
    @Select("select domain from t_website_completed")
    public abstract List<String> getList(HashMap<String,Object> params);
}
