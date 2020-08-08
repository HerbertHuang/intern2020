package com.example.user.service;

import com.example.user.dao.UserDao;
import com.example.user.entity.UserEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserDao userDao;

    @Override
    public List<UserEntity> queryAll(Map map) {
        return userDao.queryAll(map);
    }

    @Override
    public void addUser(UserEntity userEntity) {
        userDao.addUser(userEntity);
    }

    @Override
    public void delUser(int id) {
        userDao.delUser(id);
    }

    @Override
    public void updateUser(UserEntity userEntity) {
        userDao.updateUser(userEntity);
    }

    @Override
    public UserEntity queryUserById(int id) {
        return userDao.queryUserById(id);
    }
}
