package com.kk.pethome.user.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.user.mapper.PetProfileMapper;
import com.kk.pethome.user.pojo.PetProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/pet")
public class PetProfileController {

    @Autowired
    PetProfileMapper petProfileMapper;

    /** 查询用户的宠物列表 */
    @GetMapping
    public jsonresult listByUserId(@RequestParam Long userId) {
        List<PetProfile> pets = petProfileMapper.findByUserId(userId);
        return jsonresult.success(pets);
    }

    /** 根据ID查询宠物详情 */
    @GetMapping("/{id}")
    public jsonresult findById(@PathVariable Long id) {
        PetProfile pet = petProfileMapper.findById(id);
        if (pet == null) return jsonresult.fail().setMessage("未找到宠物信息");
        return jsonresult.success(pet);
    }

    /** 新增宠物档案 */
    @PostMapping
    public jsonresult create(@RequestBody PetProfile pet) {
        pet.setCreateTime(new Date());
        petProfileMapper.insert(pet);
        return jsonresult.success(pet);
    }

    /** 更新宠物档案 */
    @PutMapping("/{id}")
    public jsonresult update(@PathVariable Long id, @RequestBody PetProfile pet) {
        pet.setId(id);
        petProfileMapper.update(pet);
        return jsonresult.success(pet);
    }

    /** 删除宠物档案 */
    @DeleteMapping("/{id}")
    public jsonresult delete(@PathVariable Long id) {
        petProfileMapper.deleteById(id);
        return jsonresult.success(null);
    }
}
