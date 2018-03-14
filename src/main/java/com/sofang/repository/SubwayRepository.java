package com.sofang.repository;

import java.util.List;

import com.sofang.entity.Subway;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by gegf
 */
public interface SubwayRepository extends CrudRepository<Subway, Long>{

    /**
     * 通过城市查询地铁线路
     * @param cityEnName
     * @return
     */
    List<Subway> findAllByCityEnName(String cityEnName);
}
