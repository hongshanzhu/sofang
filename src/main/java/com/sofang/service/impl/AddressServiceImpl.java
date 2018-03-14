package com.sofang.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sofang.base.Level;
import com.sofang.base.ServiceMultiResult;
import com.sofang.entity.Subway;
import com.sofang.entity.SupportAddress;
import com.sofang.repository.SubwayRepository;
import com.sofang.repository.SupportAddressRepository;
import com.sofang.service.AddressService;
import com.sofang.web.dto.SubwayDTO;
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
    public Map<Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        return null;
    }
}
