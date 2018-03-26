package com.sofang.service;

import com.sofang.base.ServiceMultiResult;
import com.sofang.base.ServiceResult;
import com.sofang.web.dto.HouseDTO;
import com.sofang.web.form.DataTableSearch;
import com.sofang.web.form.HouseForm;
import com.sofang.web.form.RentFilter;

/**
 * 房源管理接口
 * Created by gegf
 */
public interface HouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);

    /**
     * 管理员查询房源列表
     * @param search
     * @return
     */
    ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch search);

    /**
     * 查询完整的房源信息
     * @param id
     * @return
     */
    ServiceResult<HouseDTO> findCompleteOne(Long id);

    /**
     * 查询房源信息集
     * @param rentFilter
     * @return
     */
    ServiceMultiResult<HouseDTO> query(RentFilter rentFilter);

}
