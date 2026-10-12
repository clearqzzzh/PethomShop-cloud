package com.kk.pethome.user.mapper;

import com.kk.pethome.user.pojo.PetProfile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PetProfileMapper {

    @Select("SELECT * FROM t_pet_profile WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PetProfile> findByUserId(Long userId);

    @Select("SELECT * FROM t_pet_profile WHERE id = #{id}")
    PetProfile findById(Long id);

    @Insert("INSERT INTO t_pet_profile (user_id, name, species, breed, gender, birthday, weight, avatar, is_neutered, health_notes, create_time) " +
            "VALUES (#{userId}, #{name}, #{species}, #{breed}, #{gender}, #{birthday}, #{weight}, #{avatar}, #{isNeutered}, #{healthNotes}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PetProfile pet);

    @Update("UPDATE t_pet_profile SET name = #{name}, species = #{species}, breed = #{breed}, gender = #{gender}, " +
            "birthday = #{birthday}, weight = #{weight}, avatar = #{avatar}, is_neutered = #{isNeutered}, health_notes = #{healthNotes} " +
            "WHERE id = #{id}")
    void update(PetProfile pet);

    @Delete("DELETE FROM t_pet_profile WHERE id = #{id}")
    void deleteById(Long id);
}
