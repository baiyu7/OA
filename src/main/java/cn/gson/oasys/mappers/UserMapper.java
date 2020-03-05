package cn.gson.oasys.mappers;

import cn.gson.oasys.model.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserMapper {
    //保存用户
    public int updateFatherId(@Param("user") User user);

    //根据用户保存更改islock
    public int updateIsLock(@Param("user") User user);
}
