package com.sofang.repository;

import java.util.List;

import com.sofang.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by gegf.
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetail, Long>{

    HouseDetail findByHouseId(Long houseId);

    List<HouseDetail> findAllByHouseIdIn(List<Long> houseIds);
}
