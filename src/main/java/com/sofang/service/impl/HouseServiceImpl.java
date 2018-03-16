package com.sofang.service.impl;

import com.sofang.base.LoginUserUtil;
import com.sofang.base.ServiceMultiResult;
import com.sofang.base.ServiceResult;
import com.sofang.entity.House;
import com.sofang.repository.HouseRepository;
import com.sofang.service.HouseService;
import com.sofang.web.dto.HouseDTO;
import com.sofang.web.form.HouseForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Override
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        House house = new House();
        modelMapper.map(houseForm, house);
        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUser());
        houseRepository.save(house);
        //TODO HouseDetail
        return null;
    }
}
