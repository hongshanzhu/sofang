package com.sofang;

import com.sofang.base.ServiceMultiResult;
import com.sofang.service.search.SearchService;
import com.sofang.web.form.RentFilter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchServiceTests extends ApplicationTests {

    @Autowired
    private SearchService searchService;

    @Test
    public void testIndex(){
        Long houseid = 15L;
        boolean result = searchService.index(houseid);
        Assert.assertTrue(result);
    }

    @Test
    public void testQuery() {
        RentFilter rentSearch = new RentFilter();
        rentSearch.setCityEnName("bj");
        rentSearch.setStart(0);
        rentSearch.setSize(10);
        rentSearch.setKeywords("国贸");
        ServiceMultiResult<Long> serviceResult = searchService.query(rentSearch);
        Assert.assertTrue(serviceResult.getTotal() > 0);
    }
}
