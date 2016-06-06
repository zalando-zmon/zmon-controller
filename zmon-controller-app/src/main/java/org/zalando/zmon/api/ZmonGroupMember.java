package org.zalando.zmon.api;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jmussler on 11/11/14.
 */
public class ZmonGroupMember {
    public String email;
    public String id;
    public String name;
    public final Set<String> phones;

    public ZmonGroupMember() {
        this.phones = new HashSet<>();
    }
}
