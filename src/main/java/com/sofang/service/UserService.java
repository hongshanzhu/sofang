package com.sofang.service;

import com.sofang.entity.User;

/**
 * 用户Service接口
 * Created by gegf
 */
public interface UserService {

    User findUserByName(String username);
}
