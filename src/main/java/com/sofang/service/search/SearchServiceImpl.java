package com.sofang.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.sofang.entity.House;
import com.sofang.repository.HouseRepository;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private TransportClient esClient;

    private static final String INDEX_NAME = "fang";

    private static final String INDEX_TYPE = "house";

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void index(Long houseId) {
        House house = houseRepository.findOne(houseId);
        Preconditions.checkNotNull(house, "Index house " + houseId + " dose not exsit!");
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);

    }

    private boolean create(HouseIndexTemplate indexTemplate){
        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            logger.debug("Create index with house " + indexTemplate.getHouseId());
            return response.status() == RestStatus.CREATED;
        } catch (JsonProcessingException e) {
            logger.error("Error to Index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }


    @Override
    public void remove(Long houseId) {

    }
}
