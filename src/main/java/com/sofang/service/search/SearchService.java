package com.sofang.service.search;


import com.sofang.base.ServiceMultiResult;
import com.sofang.web.form.RentFilter;

/**
 * 检索接口
 */
public interface SearchService {
    /**
     * 索引目标房源
     * @param houseId
     */
    boolean index(Long houseId);

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


}
