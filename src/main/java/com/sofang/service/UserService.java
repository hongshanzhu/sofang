package com.sofang.service;

import com.sofang.base.ServiceResult;
import com.sofang.entity.User;
import com.sofang.web.dto.UserDTO;

/**
 * 用户Service接口
 * Created by gegf
 */
public interface UserService {

    User findUserByName(String username);

    ServiceResult<UserDTO> findById(Long useId);
}
