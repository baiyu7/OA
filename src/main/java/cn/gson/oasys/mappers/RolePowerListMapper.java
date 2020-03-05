package cn.gson.oasys.mappers;

import cn.gson.oasys.model.entity.role.Role;
import cn.gson.oasys.model.entity.role.Rolepowerlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePowerListMapper {


    //删除角色RolePowerList
    public int deleteRoleRolePowerList(@Param("role") Role role);


}
