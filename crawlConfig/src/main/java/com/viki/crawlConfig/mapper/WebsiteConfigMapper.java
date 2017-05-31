package com.viki.crawlConfig.mapper;

import com.viki.crawlConfig.bean.WebsiteConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Viki on 2017/5/26.
 */
@Mapper
public interface WebsiteConfigMapper {

    @Select("select * from t_website_config")
    public abstract List<WebsiteConfig> getList(HashMap<String,Object> params);

    @Insert("insert into t_website_config(\n" +
            "entranceUrl,titleXpath,postdateXpath,postdateFormat,contentXpath,encoding," +
            "webName,groupName,domain,crawling_interval,stopSeconds,urlSourceNorm,urlPattern," +
            "urlREPOrigin,urlREPReplacement,urlPrefix,`status`,postDate_is_from_url,crawlDepth, sampleUrl" +
            ")values(#{cfg.entranceUrl}, #{cfg.titleXpath}, #{cfg.postdateXpath}, #{cfg.postdateFormat}, #{cfg.contentXpath}, #{cfg.encoding}, " +
            "#{cfg.webName}, #{cfg.groupName}, #{cfg.domain}, #{cfg.crawling_interval}, #{cfg.stopSeconds}, #{cfg.urlSourceNorm}, #{cfg.urlPattern}," +
            " #{cfg.urlREPOrigin}, #{cfg.urlREPReplacement}, #{cfg.urlPrefix}, #{cfg.status}, #{cfg.postDate_is_from_url}, #{cfg.crawlDepth}, #{cfg.sampleUrl})")
    public int insert(@Param("cfg") WebsiteConfig websiteConfig);
}
