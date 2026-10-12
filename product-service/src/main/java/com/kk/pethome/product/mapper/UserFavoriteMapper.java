package com.kk.pethome.product.mapper;

import com.kk.pethome.product.pojo.UserFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {

    @Select("SELECT * FROM t_user_favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "category", column = "category"),
        @Result(property = "productName", column = "product_name"),
        @Result(property = "productImage", column = "product_image"),
        @Result(property = "productPrice", column = "product_price"),
        @Result(property = "createTime", column = "create_time")
    })
    List<UserFavorite> findByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM t_user_favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int exists(@Param("userId") Long userId, @Param("productId") Long productId);

    @Insert("INSERT INTO t_user_favorite(user_id, product_id, category, product_name, product_image, product_price, create_time) " +
            "VALUES(#{userId}, #{productId}, #{category}, #{productName}, #{productImage}, #{productPrice}, #{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(UserFavorite fav);

    @Delete("DELETE FROM t_user_favorite WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("DELETE FROM t_user_favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
