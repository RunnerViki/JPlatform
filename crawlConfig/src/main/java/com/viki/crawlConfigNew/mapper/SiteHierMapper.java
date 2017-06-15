package com.viki.crawlConfigNew.mapper;

import com.viki.crawlConfigNew.bean.SiteHier;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Viki on 2017/6/5.
 * Function: TODO
 */
@Mapper
public interface SiteHierMapper {

    @Insert("insert into t_site_hier(hierName, hierFullName, parentHier, rootHier, depthCurrentHier, depthInTotal, document)" +
            "values (#{siteHier.hierName},#{siteHier.fullName},#{siteHier.parentHier.id},#{siteHier.rootHier.id},#{siteHier.depthCurrentHier},#{siteHier.depthInTotal},#{siteHier.docContent})" +
            "")
    @Options(useGeneratedKeys=true, keyProperty="siteHier.id")
    public void insert(@Param("siteHier") SiteHier siteHier);

    @Update("update t_site_hier set document = #{siteHier.docContent} where id = #{siteHier.id}")
    public void updateDocument(@Param("siteHier") SiteHier siteHier);


    @Select("select id, hierName, parentHier, rootHier, depthCurrentHier, depthInTotal, document from t_site_hier order by depthCurrentHier")
    public List<HashMap<String,String>> select();
}
