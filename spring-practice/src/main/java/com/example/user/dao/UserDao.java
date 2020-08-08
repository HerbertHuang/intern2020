package com.example.user.dao;

import com.example.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao {

    public List<UserEntity> queryAll(@Param("content") Map map);

    public void addUser(UserEntity userEntity);

    public void delUser(int id);

    public void updateUser(UserEntity userEntity);

    public UserEntity queryUserById(int id);

}
