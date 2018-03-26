package com.sofang.service.impl;

import com.sofang.base.ServiceMultiResult;
import com.sofang.base.ServiceResult;
import com.sofang.service.ElasticsearchService;
import com.sofang.web.form.RentFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService{

    @Override
    public void index(Long houseId) {

    }

    @Override
    public void remove(Long houseId) {

    }

    @Override
    public ServiceMultiResult<Long> query(RentFilter rentFilter) {
        return null;
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix) {
        return null;
    }

    @Override
    public ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
        return null;
    }

    @Override
    public ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size) {
        return null;
    }
}
