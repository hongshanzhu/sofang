package com.sofang.service;

import com.sofang.base.ServiceMultiResult;
import com.sofang.base.ServiceResult;
import com.sofang.web.form.RentFilter;

import java.util.List;

public interface ElasticsearchService {
    /**
     * 索引目标房源
     * @param houseId
     */
    void index(Long houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    void remove(Long houseId);

    /**
     * 查询房源接口
     * @param rentFilter
     * @return
     */
    ServiceMultiResult<Long> query(RentFilter rentFilter);

    /**
     * 获取补全建议关键词
     */
    ServiceResult<List<String>> suggest(String prefix);

    /**
     * 聚合特定小区的房间数
     */
    ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);


    /**
     * 城市级别查询
     * @return
     */
    ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy,
                                      String orderDirection, int start, int size);
}
