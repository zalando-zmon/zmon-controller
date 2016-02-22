package de.zalando.zmon.rest;

import org.junit.Test;

/**
 * Created by jmussler on 18.02.16.
 */


public class BasicRestTest {
    @Test
    public void testRegex() {
        String search = "title:stups*";
        String dbSearch = search.replace("title:", "").replace("*", "");

        assert(dbSearch.equals("stups"));
    }
}
