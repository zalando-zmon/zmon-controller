package org.zalando.zmon.api.domain.entity.specific;

import java.util.HashSet;
import java.util.Set;

public class CheckTiersEntity {
    private String id;
    private Tiers data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Tiers getData() {
        return data;
    }

    public void setData(Tiers data) {
        this.data = data;
    }

    public static class Tiers {
        private Set<Integer> critical = new HashSet<>();
        private Set<Integer> important = new HashSet<>();

        public Set<Integer> getCritical() {
            return critical;
        }

        public void setCritical(Set<Integer> critical) {
            this.critical = critical;
        }

        public Set<Integer> getImportant() {
            return important;
        }

        public void setImportant(Set<Integer> important) {
            this.important = important;
        }
    }
}
