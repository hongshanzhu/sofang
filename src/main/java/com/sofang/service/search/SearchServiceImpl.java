package com.sofang.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.sofang.base.HouseSort;
import com.sofang.base.RentValueBlock;
import com.sofang.base.ServiceMultiResult;
import com.sofang.entity.House;
import com.sofang.entity.HouseTag;
import com.sofang.repository.HouseRepository;
import com.sofang.repository.HouseTagRepository;
import com.sofang.web.form.RentFilter;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private TransportClient esClient;

    private static final String INDEX_NAME = "fang";

    private static final String INDEX_TYPE = "house";

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseTagRepository tagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean index(Long houseId) {
        House house = houseRepository.findOne(houseId);
        Preconditions.checkNotNull(house, "Index house " + houseId + " dose not exsit!");
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);

        List<HouseTag> tags = tagRepository.findAllByHouseId(houseId);
        if(!CollectionUtils.isEmpty(tags)){
            List<String> tagStrings = Lists.newArrayList();
            tags.forEach(tag -> tagStrings.add(tag.getName()));
            houseIndexTemplate.setTags(tagStrings);
        }

        SearchRequestBuilder builder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,
                houseId));
        logger.debug(builder.toString());
        SearchResponse response = builder.get();
        long totalHits = response.getHits().totalHits;
        boolean success;
        if(totalHits == 0){
            success = create(houseIndexTemplate);
        }else if(totalHits == 1){
            String esId = response.getHits().getAt(0).getId();
            success = update(esId, houseIndexTemplate);
        }else{
            success = deleteAndCreate(totalHits, houseIndexTemplate);
        }

        if(success){
            logger.debug("Index success this house " + houseId);
        }
        return success;
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

    private boolean update(String esId, HouseIndexTemplate indexTemplate) {
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            logger.debug("Update index with house: " + indexTemplate.getHouseId());
            return response.status() == RestStatus.OK;
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit, HouseIndexTemplate indexTemplate){
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId())).source(INDEX_NAME);
        logger.debug("Delete by query for house : " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            logger.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(indexTemplate);
        }
    }


    @Override
    public void remove(Long houseId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId)).source(INDEX_NAME);
        logger.debug("Delete by query for house : " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.debug("Delete total :" + deleted);
    }

    @Override
    public ServiceMultiResult<Long> query(RentFilter rentSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.filter(
                QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName())
        );

        if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName())
            );
        }

        RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
        if (!RentValueBlock.ALL.equals(area)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
            if (area.getMax() > 0) {
                rangeQueryBuilder.lte(area.getMax());
            }
            if (area.getMin() > 0) {
                rangeQueryBuilder.gte(area.getMin());
            }
            boolQuery.filter(rangeQueryBuilder);
        }

        RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        if (!RentValueBlock.ALL.equals(price)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
            if (price.getMax() > 0) {
                rangeQuery.lte(price.getMax());
            }
            if (price.getMin() > 0) {
                rangeQuery.gte(price.getMin());
            }
            boolQuery.filter(rangeQuery);
        }

        if (rentSearch.getDirection() > 0) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.DIRECTION, rentSearch.getDirection())
            );
        }

        if (rentSearch.getRentWay() > -1) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay())
            );
        }

//        boolQuery.must(
//                QueryBuilders.matchQuery(HouseIndexKey.TITLE, rentSearch.getKeywords())
//                        .boost(2.0f)
//        );

        boolQuery.must(
                QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
                        HouseIndexKey.TITLE,
                        HouseIndexKey.TRAFFIC,
                        HouseIndexKey.DISTRICT,
                        HouseIndexKey.ROUND_SERVICE,
                        HouseIndexKey.SUBWAY_LINE_NAME,
                        HouseIndexKey.SUBWAY_STATION_NAME
                ));

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(
                        HouseSort.getSortKey(rentSearch.getOrderBy()),
                        SortOrder.fromString(rentSearch.getOrderDirection())
                )
                .setFrom(rentSearch.getStart())
                .setSize(rentSearch.getSize())
                .setFetchSource(HouseIndexKey.HOUSE_ID, null);

        logger.debug(requestBuilder.toString());

        List<Long> houseIds = new ArrayList<>();
        SearchResponse response = requestBuilder.get();
        if (response.status() != RestStatus.OK) {
            logger.warn("Search status is no ok for " + requestBuilder);
            return new ServiceMultiResult<>(0, houseIds);
        }

        for (SearchHit hit : response.getHits()) {
            System.out.println(hit.getSource());
            houseIds.add(Longs.tryParse(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
        }

        return new ServiceMultiResult<>(response.getHits().totalHits, houseIds);
    }
}
