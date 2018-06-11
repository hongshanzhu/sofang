package com.sofang.service.search;

import com.google.common.base.Preconditions;
import com.sofang.entity.House;
import com.sofang.repository.HouseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchServiceImpl implements SearchService {

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void index(Long houseId) {
        House house = houseRepository.findOne(houseId);
        Preconditions.checkNotNull(house, "Index house " + houseId + " dose not exsit!");
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);

    }

    @Override
    public void remove(Long houseId) {

    }
}
