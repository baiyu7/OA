package cn.gson.oasys.mappers;

import cn.gson.oasys.model.entity.user.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeptMapper {
    //修改部门经理字段
    public int update(@Param("dept") Dept dept);

    //修改总裁的id
    public int updateMaxId(@Param("dept") Dept dept);
}
