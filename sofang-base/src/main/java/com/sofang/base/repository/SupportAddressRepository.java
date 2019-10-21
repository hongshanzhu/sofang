package com.sofang.base.repository;

import com.sofang.base.entity.SupportAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by gegf
 */
public interface SupportAddressRepository extends CrudRepository<SupportAddress, Long>{

    /**
     * 通过行政级别获取所有信息
     * @param level
     * @return
     */
    List<SupportAddress> findAllByLevel(String level);

    SupportAddress findByEnNameAndLevel(String enName, String level);

    SupportAddress findByEnNameAndBelongTo(String enName, String belongTo);

    List<SupportAddress> findAllByLevelAndBelongTo(String level, String belongTo);

}
