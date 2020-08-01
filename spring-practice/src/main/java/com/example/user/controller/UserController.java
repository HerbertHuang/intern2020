package com.example.user.controller;

import com.example.user.entity.UserEntity;
import com.example.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/queryAll", method = RequestMethod.GET)
    @ApiOperation(value="整表查询")
    public List<UserEntity> queryAll(Map map){
        return userService.queryAll(map);
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ApiOperation(value="增加")
    public void addUser(UserEntity userEntity){
       userService.addUser(userEntity);
    }

    @RequestMapping(value = "/delUser", method = RequestMethod.DELETE)
    @ApiOperation(value="删除")
    public void delUser(int id){
        userService.delUser(id);
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    @ApiOperation(value="修改")
    public void updateUser(UserEntity userEntity){
        userService.updateUser(userEntity);
    }

    @RequestMapping(value = "/queryUserById", method = RequestMethod.GET)
    @ApiOperation(value="查询")
    public UserEntity queryUserById(int id){
        return userService.queryUserById(id);
    }
}
