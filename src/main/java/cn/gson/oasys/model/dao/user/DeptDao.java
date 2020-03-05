package cn.gson.oasys.model.dao.user;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import cn.gson.oasys.model.entity.user.Dept;
import org.springframework.transaction.annotation.Transactional;

public interface DeptDao extends PagingAndSortingRepository<Dept, Long> {

    List<Dept> findByDeptId(Long id);


    @Query("select de.deptName from Dept de where de.deptId=:id")
    String findname(@Param("id") Long id);

    @Modifying@Transactional
    @Query("update Dept de set de.deptmanager =?1 where de.deptId = ?2")
    int update(Long deptmanager,Long deptId);


    @Override@Modifying@Transactional
    <S extends Dept> S save(S s);
}
