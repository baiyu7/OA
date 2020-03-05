package cn.gson.oasys.mappers;

import cn.gson.oasys.model.entity.role.Role;
import cn.gson.oasys.model.entity.user.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface RoleMapper {
        //删除角色Role
        public int deleteRole(@Param("role") Role role);


}
