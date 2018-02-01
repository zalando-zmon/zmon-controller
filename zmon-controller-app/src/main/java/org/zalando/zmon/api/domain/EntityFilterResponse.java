package org.zalando.zmon.api.domain;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jmussler on 10.01.17.
 */
public class EntityFilterResponse {

    public EntityFilterResponse() {

    }

    public EntityFilterResponse(int count) {
        this.count = count;
    }

    public EntityFilterResponse(String message) {
        this.message = message;
    }


    public static class SimpleEntity {
        public SimpleEntity() {

        }

        public SimpleEntity(String id, String type) {
            this.id = id;
            this.type = type;
        }

        public String id;
        public String type;
    }

    public int count = 0;
    public final Collection<SimpleEntity> entities = new ArrayList<>(25);
    public String message = new String("");
}
