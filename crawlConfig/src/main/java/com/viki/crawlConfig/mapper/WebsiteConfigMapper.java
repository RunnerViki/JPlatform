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

    @Select("select * from t_website_config order by domain desc")
    public abstract List<WebsiteConfig> getList(HashMap<String,Object> params);

    @Insert("insert into t_website_config(\n" +
            "entranceUrl,titleXpath,postdateXpath,postdateFormat,contentXpath,encoding," +
            "webName,groupName,domain,crawling_interval,stopSeconds,urlSourceNorm,urlPattern," +
            "urlREPOrigin,urlREPReplacement,urlPrefix,`status`,postDate_is_from_url,crawlDepth, sampleUrl" +
            ")values(#{cfg.entranceUrl}, #{cfg.titleXpath}, #{cfg.postdateXpath}, #{cfg.postdateFormat}, #{cfg.contentXpath}, #{cfg.encoding}, " +
            "#{cfg.webName}, #{cfg.groupName}, #{cfg.domain}, #{cfg.crawling_interval}, #{cfg.stopSeconds}, #{cfg.urlSourceNorm}, #{cfg.urlPattern}," +
            " #{cfg.urlREPOrigin}, #{cfg.urlREPReplacement}, #{cfg.urlPrefix}, #{cfg.status}, #{cfg.postDate_is_from_url}, #{cfg.crawlDepth}, #{cfg.sampleUrl})")
    public int insert(@Param("cfg") WebsiteConfig websiteConfig);

    @Insert("insert into t_website_config(\n" +
            "entranceUrl,titleXpath,postdateXpath,postdateFormat,contentXpath,encoding," +
            "webName,groupName,domain,crawling_interval,stopSeconds,urlSourceNorm,urlPattern," +
            "urlREPOrigin,urlREPReplacement,urlPrefix,`status`,postDate_is_from_url,crawlDepth, sampleUrl, checked_status" +
            ")values(#{cfg.entranceUrl}, #{cfg.titleXpath}, #{cfg.postdateXpath}, #{cfg.postdateFormat}, #{cfg.contentXpath}, #{cfg.encoding}, " +
            "#{cfg.webName}, #{cfg.groupName}, #{cfg.domain}, #{cfg.crawling_interval}, #{cfg.stopSeconds}, #{cfg.urlSourceNorm}, #{cfg.urlPattern}," +
            " #{cfg.urlREPOrigin}, #{cfg.urlREPReplacement}, #{cfg.urlPrefix}, #{cfg.status}, #{cfg.postDate_is_from_url}, #{cfg.crawlDepth}, #{cfg.sampleUrl}, #{cfg.checked_status})" +
            "on duplicate key update " +
            "entranceUrl = values(entranceUrl),  titleXpath = values(titleXpath), " +
            "postdateXpath = values(postdateXpath),  postdateFormat = values(postdateFormat), " +
            "contentXpath = values(contentXpath),  encoding = values(encoding), " +
            "webName = values(webName),  groupName = values(groupName), " +
            "domain = values(domain),  crawling_interval = values(crawling_interval), " +
            "stopSeconds = values(stopSeconds),  urlSourceNorm = values(urlSourceNorm), " +
            "urlPattern = values(urlPattern),  urlREPOrigin = values(urlREPOrigin), " +
            "urlREPReplacement = values(urlREPReplacement),  urlPrefix = values(urlPrefix), " +
            "`status` = values(`status`),  postDate_is_from_url = values(postDate_is_from_url), " +
            "crawlDepth = values(crawlDepth), sampleUrl = values(sampleUrl) , checked_status = values(checked_status) "
    )
    public int update(@Param("cfg") WebsiteConfig websiteConfig);
}
