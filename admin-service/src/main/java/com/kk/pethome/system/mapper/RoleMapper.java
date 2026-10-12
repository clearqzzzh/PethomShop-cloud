package com.kk.pethome.system.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.system.pojo.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT * FROM t_role")
    List<Role> lookAll();

    @Select("SELECT * FROM t_role WHERE id=#{id}")
    Role lookById(Long id);

    @Select("SELECT COUNT(*) FROM t_role")
    Integer loadcount(BaseQuery<Role> baseQuery);

    @Select("SELECT * FROM t_role LIMIT #{currentPage}, #{pageSize}")
    List<Role> lookPage(BaseQuery<Role> baseQuery);

    @Insert("INSERT INTO t_role(name, sn) VALUES(#{name}, #{sn})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void Add(Role role);

    @Update("UPDATE t_role SET name=#{name}, sn=#{sn} WHERE id=#{id}")
    void Update(Role role);

    @Delete("DELETE FROM t_role WHERE id=#{id}")
    void Delete(Long id);

    @Delete("<script>DELETE FROM t_role WHERE id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    void DeleteBatch(@Param("ids") List<Long> ids);

    // Get all permission IDs assigned to a role
    @Select("SELECT permission_id FROM t_role_permission WHERE role_id=#{roleId}")
    List<Long> getPermissionIdsByRoleId(Long roleId);

    // Get all menu IDs assigned to a role
    @Select("SELECT menu_id FROM t_role_menu WHERE role_id=#{roleId}")
    List<Long> getMenuIdsByRoleId(Long roleId);

    // Assign permissions to a role (delete old + insert new)
    @Delete("DELETE FROM t_role_permission WHERE role_id=#{roleId}")
    void deleteRolePermissions(Long roleId);

    @Insert("INSERT INTO t_role_permission(role_id, permission_id) VALUES(#{roleId}, #{permissionId})")
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    // Assign menus to a role (delete old + insert new)
    @Delete("DELETE FROM t_role_menu WHERE role_id=#{roleId}")
    void deleteRoleMenus(Long roleId);

    @Insert("INSERT INTO t_role_menu(role_id, menu_id) VALUES(#{roleId}, #{menuId})")
    void insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
}
