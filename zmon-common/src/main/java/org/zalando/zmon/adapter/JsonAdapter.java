package org.zalando.zmon.adapter;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.zalando.zmon.domain.Parameter;
import org.zalando.zmon.util.ObjectMapperProvider;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Created by pribeiro on 24/06/14.
 */
public class JsonAdapter extends XmlAdapter<String, Map<String, Parameter>> {

    private static final TypeReference<Map<String, Parameter>> INPUT_TYPE =
        new TypeReference<Map<String, Parameter>>() { };

    @Override
    public Map<String, Parameter> unmarshal(final String v) throws Exception {
        if (v == null) {
            return null;
        }

        return ObjectMapperProvider.OBJECT_MAPPER.readValue(v, INPUT_TYPE);
    }

    @Override
    public String marshal(final Map<String, Parameter> v) throws Exception {
        if (v == null) {
            return null;
        }

        return ObjectMapperProvider.OBJECT_MAPPER.writeValueAsString(v);
    }
}
