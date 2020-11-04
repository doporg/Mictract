package com.baasexample.demo.Mapper;

import com.baasexample.demo.model.dbMo.NetInfo;
import com.baasexample.demo.model.dbMo.NewNetInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Mapper
@Repository
public interface NewNetMapper {
    //insert
    @Insert("insert into newnet (namespace,ordererList,orgList,consensus,tls,createtime,status) values(#{namespace},#{ordererList},#{orgList},#{consensus},#{tls},#{createtime},#{status})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public void insertNet(NewNetInfo newNetInfo);

    //QueryByid
    @Select("select * from newnet where id = #{id}")
    public NewNetInfo findNetById (@Param("id") int id);
    @Select("select id from newnet where namespace = #{namespace}")
    public int queryId(@Param("namespace")String namespace);
    //QueryAllNet
    @Select("select * from newnet")
    public List<NewNetInfo> getAllNet();

    @Update("update newnet set status = #{status} where id = #{id}")
    public void updateNetStatu(@Param("status")int status,@Param("id") int id);
}
