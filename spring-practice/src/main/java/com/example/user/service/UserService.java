package com.example.user.service;

import com.example.user.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserService {
    public List<UserEntity> queryAll( Map map);

    public void addUser(UserEntity userEntity);

    public void delUser(int id);

    public void updateUser(UserEntity userEntity);

    public UserEntity queryUserById(int id);
}
