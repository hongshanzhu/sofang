package com.sofang.repository;

import java.util.List;

import com.sofang.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by gegf.
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {

    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    List<SubwayStation> findAllBySubwayId(Long subwayId);
}
