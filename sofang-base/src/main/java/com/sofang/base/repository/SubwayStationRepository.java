package com.sofang.base.repository;

import com.sofang.base.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

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
