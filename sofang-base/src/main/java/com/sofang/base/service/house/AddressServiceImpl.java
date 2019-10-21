package com.sofang.base.service.house;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.sofang.base.common.Level;
import com.sofang.base.common.ServiceMultiResult;
import com.sofang.base.common.ServiceResult;
import com.sofang.base.entity.Subway;
import com.sofang.base.entity.SubwayStation;
import com.sofang.base.entity.SupportAddress;
import com.sofang.base.repository.SubwayRepository;
import com.sofang.base.repository.SubwayStationRepository;
import com.sofang.base.repository.SupportAddressRepository;
import com.sofang.base.service.dto.SubwayDTO;
import com.sofang.base.service.dto.SubwayStationDTO;
import com.sofang.base.service.dto.SupportAddressDTO;
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

    @Override
    public ServiceResult<SubwayDTO> findSubway(Long subwayId) {
        if (subwayId == null) {
            return ServiceResult.notFound();
        }
        Subway subway = subwayRepository.findOne(subwayId);
        if (subway == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(subway, SubwayDTO.class));
    }

    @Override
    public ServiceResult<SubwayStationDTO> findSubwayStation(Long stationId) {
        if (stationId == null) {
            return ServiceResult.notFound();
        }
        SubwayStation station = subwayStationRepository.findOne(stationId);
        if (station == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(station, SubwayStationDTO.class));
    }

    @Override
    public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
        if (cityEnName == null) {
            return ServiceResult.notFound();
        }

        SupportAddress supportAddress = supportAddressRepository.findByEnNameAndLevel(cityEnName, Level.CITY.getValue());
        if (supportAddress == null) {
            return ServiceResult.notFound();
        }

        SupportAddressDTO addressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
        return ServiceResult.of(addressDTO);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityName) {
        if (cityName == null) {
            return new ServiceMultiResult<>(0, null);
        }

        List<SupportAddressDTO> result = Lists.newArrayList();

        List<SupportAddress> regions = supportAddressRepository.findAllByLevelAndBelongTo(Level.REGION
                .getValue(), cityName);
        for (SupportAddress region : regions) {
            result.add(modelMapper.map(region, SupportAddressDTO.class));
        }
        return new ServiceMultiResult<>(regions.size(), result);
    }

}
