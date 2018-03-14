package com.sofang.service;

import java.util.Map;

import com.sofang.base.Level;

import com.sofang.base.ServiceMultiResult;
import com.sofang.web.dto.SupportAddressDTO;

/**
 * 地址服务接口
 * Created by gegf.
 */
public interface AddressService {
    /**
     * 获取所有支持的城市列表
     * @return
     */
    ServiceMultiResult<SupportAddressDTO> findAllCities();

    /**
     * 根据英文简写获取具体区域的信息
     * @param cityEnName
     * @param regionEnName
     * @return
     */
    Map<Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    /**
     * 获取城市下的所有区/县
     * @param cityEnName
     * @return
     */
    ServiceMultiResult<SupportAddressDTO> findAllRegionByCityName(String cityEnName);
}

