package com.sofang.service;

import com.sofang.base.ServiceResult;
import com.sofang.web.dto.HouseDTO;
import com.sofang.web.form.HouseForm;

/**
 * 房源管理接口
 * Created by gegf
 */
public interface HouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);

}
