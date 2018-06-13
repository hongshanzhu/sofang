package com.sofang;

import com.sofang.service.search.SearchService;
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
}
