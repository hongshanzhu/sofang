package com.sofang.service;

import java.util.List;
import java.util.Map;

import com.sofang.base.Level;

import com.sofang.base.ServiceMultiResult;
import com.sofang.base.ServiceResult;
import com.sofang.web.dto.HouseDTO;
import com.sofang.web.dto.SubwayDTO;
import com.sofang.web.dto.SubwayStationDTO;
import com.sofang.web.dto.SupportAddressDTO;
import com.sofang.web.form.RentFilter;

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

    /**
     * 获取城市下的地铁线路
     * @param cityEnName
     * @return
     */
    List<SubwayDTO> findAllSubwayByCity(String cityEnName);

    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    List<SubwayStationDTO> findAllStationBySubway(Long subwayId);

    public ServiceResult<SubwayDTO> findSubway(Long subwayId);

    ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId);

    ServiceResult<SupportAddressDTO> findCity(String cityEnName);

    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName);

}

