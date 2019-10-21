package com.sofang.base.service.user;

import com.sofang.base.common.ServiceResult;
import com.sofang.base.entity.User;
import com.sofang.base.service.dto.UserDTO;

/**
 * 用户Service接口
 * Created by gegf
 */
public interface UserService {

    User findUserByName(String username);

    ServiceResult<UserDTO> findById(Long useId);
}
