package com.sofang.base.service.search;


import com.sofang.base.common.ServiceMultiResult;
import com.sofang.base.common.ServiceResult;
import com.sofang.base.service.form.RentFilter;

import java.util.List;

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

    /**
     * 获取自动补全的建议关键词
     * @return
     */
    ServiceResult<List<String>> suggest(String prefix);


}
