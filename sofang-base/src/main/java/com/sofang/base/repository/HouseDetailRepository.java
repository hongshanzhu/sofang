package com.sofang.base.repository;

import com.sofang.base.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by gegf.
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetail, Long>{

    HouseDetail findByHouseId(Long houseId);

    List<HouseDetail> findAllByHouseIdIn(List<Long> houseIds);
}
