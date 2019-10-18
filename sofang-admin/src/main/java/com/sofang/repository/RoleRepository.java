package com.sofang.repository;


import com.sofang.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by gegf
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    List<Role> findRolesByUserId(Long userId);
}
