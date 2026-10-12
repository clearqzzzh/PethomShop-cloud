package com.kk.pethome.order.mapper;

import com.kk.pethome.order.pojo.BrowseHistory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BrowseHistoryMapper {

    @Select("SELECT * FROM t_browse_history WHERE user_id = #{userId} ORDER BY browse_time DESC LIMIT #{limit}")
    List<BrowseHistory> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    @Insert("INSERT INTO t_browse_history (user_id, product_id, product_name, product_image, product_price, category, browse_time) " +
            "VALUES (#{userId}, #{productId}, #{productName}, #{productImage}, #{productPrice}, #{category}, #{browseTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BrowseHistory h);

    @Delete("DELETE FROM t_browse_history WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("DELETE FROM t_browse_history WHERE user_id = #{userId}")
    int deleteByUser(@Param("userId") Long userId);
}
