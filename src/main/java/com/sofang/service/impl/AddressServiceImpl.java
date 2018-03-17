package com.sofang.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sofang.base.Level;
import com.sofang.base.ServiceMultiResult;
import com.sofang.entity.Subway;
import com.sofang.entity.SubwayStation;
import com.sofang.entity.SupportAddress;
import com.sofang.repository.SubwayRepository;
import com.sofang.repository.SubwayStationRepository;
import com.sofang.repository.SupportAddressRepository;
import com.sofang.service.AddressService;
import com.sofang.web.dto.SubwayDTO;
import com.sofang.web.dto.SubwayStationDTO;
import com.sofang.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> addresses = supportAddressRepository.findAllByLevel(Level.CITY.getValue());
        List<SupportAddressDTO> addressDTOS = Lists.newArrayList();
        addresses.forEach(address -> {
            addressDTOS.add(modelMapper.map(address, SupportAddressDTO.class));
        });
        return new ServiceMultiResult<>(addressDTOS.size(), addressDTOS);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionByCityName(String cityEnName) {
        if(Strings.isNullOrEmpty(cityEnName)){
            return new ServiceMultiResult<>(0, null);
        }
        List<SupportAddressDTO> result = Lists.newArrayList();
        List<SupportAddress> regions = supportAddressRepository.findAllByLevelAndBelongTo(Level.REGION
                .getValue(), cityEnName);
        regions.forEach(region -> {
            result.add(modelMapper.map(region, SupportAddressDTO.class));
        });
        return new ServiceMultiResult<>(regions.size(), result);
    }

    @Override
    public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
        List<SubwayDTO> result = Lists.newArrayList();
        List<Subway> subways = subwayRepository.findAllByCityEnName(cityEnName);
        subways.forEach(subway -> result.add(modelMapper.map(subway, SubwayDTO.class)));
        return result;
    }

    @Override
    public List<SubwayStationDTO> findAllStationBySubway(Long subwayId) {
        List<SubwayStationDTO> result = Lists.newArrayList();
        List<SubwayStation> stations = subwayStationRepository.findAllBySubwayId(subwayId);
        if (stations.isEmpty()) {
            return result;
        }

        stations.forEach(station -> result.add(modelMapper.map(station, SubwayStationDTO.class)));
        return result;
    }

    @Override
    public Map<Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<Level, SupportAddressDTO> result = Maps.newHashMap();

        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, Level.CITY
                .getValue());
        SupportAddress region = supportAddressRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

        result.put(Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
        result.put(Level.REGION, modelMapper.map(region, SupportAddressDTO.class));
        return result;
    }
}
