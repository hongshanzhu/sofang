package com.sofang.service.impl;

import com.google.common.collect.Lists;
import com.sofang.base.HouseStatus;
import com.sofang.base.LoginUserUtil;
import com.sofang.base.ServiceMultiResult;
import com.sofang.base.ServiceResult;
import com.sofang.entity.*;
import com.sofang.repository.*;
import com.sofang.service.HouseService;
import com.sofang.web.dto.HouseDTO;
import com.sofang.web.dto.HouseDetailDTO;
import com.sofang.web.dto.HousePictureDTO;
import com.sofang.web.form.DataTableSearch;
import com.sofang.web.form.HouseForm;
import com.sofang.web.form.PhotoForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;


    @Override
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        HouseDetail detail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidtionResult = wrapperDetailInfo(detail, houseForm);
        if (subwayValidtionResult != null) {
            return subwayValidtionResult;
        }

        House house = new House();
        modelMapper.map(houseForm, house);
        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUser());
        houseRepository.save(house);
        //TODO HouseDetail
        detail.setHouseId(house.getId());
        detail = houseDetailRepository.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        Iterable<HousePicture> housePictures = housePictureRepository.save(pictures);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);

        List<HousePictureDTO> pictureDTOS = Lists.newArrayList();
        housePictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            houseTagRepository.save(houseTags);
            houseDTO.setTags(tags);
        }

        return new ServiceResult<HouseDTO>(true, null, houseDTO);
    }

    /**
     * 图片对象列表信息填充
     * @param form
     * @param houseId
     * @return
     */
    private List<HousePicture> generatePictures(HouseForm form, Long houseId) {
        List<HousePicture> pictures = Lists.newArrayList();
        if (form.getPhotos() == null || form.getPhotos().isEmpty()) {
            return pictures;
        }

        for (PhotoForm photoForm : form.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(cdnPrefix);
            picture.setPath(photoForm.getPath());
            picture.setWidth(photoForm.getWidth());
            picture.setHeight(photoForm.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }

    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {
        Subway subway = subwayRepository.findOne(houseForm.getSubwayLineId());
        if (subway == null) {
            return new ServiceResult<>(false, "Not valid subway line!");
        }

        SubwayStation subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null || subway.getId() != subwayStation.getSubwayId()) {
            return new ServiceResult<>(false, "Not valid subway station!");
        }

        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());

        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        return null;
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch search) {
        List<HouseDTO> houseDTOS = Lists.newArrayList();

        Sort sort = new Sort(Sort.Direction.fromString(search.getDirection()), search.getOrderBy());
        int page = search.getStart() / search.getLength(); //第几页
        Pageable pageable = new PageRequest(page, search.getLength(), sort);

        Specification<House> specification = (root, query, cb)->{
            //基础条件 账户为admin 房源状态不是删除
             javax.persistence.criteria.Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUser());
             predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));

             if(search.getCity() != null){
                 predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), search.getCity()));
             }
             if(search.getStatus() != null){
                 predicate = cb.and(predicate, cb.equal(root.get("status"), search.getStatus()));
             }
             if(search.getCreateTimeMin() != null){
                 predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), search.getCreateTimeMin()));
             }
             if (search.getCreateTimeMax() != null){
                 predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), search.getCreateTimeMax()));
             }
             if(search.getTitle() != null){
                 predicate = cb.and(predicate, cb.like(root.get("title"), "%"+search.getTitle()+"%"));
             }
             return predicate;
        };

        Page<House> houses = houseRepository.findAll(specification, pageable);

        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);
        });

        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
    }
}
