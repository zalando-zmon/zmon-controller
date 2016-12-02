package org.zalando.zmon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zalando.zmon.persistence.EntitySProcService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by jmussler on 02/12/16.
 */
@Service
public class DynamicTeamService {

    private final Logger log = LoggerFactory.getLogger(DynamicTeamService.class);

    private final EntitySProcService entityService;
    private final ObjectMapper mapper;
    private TeamConfiguration config = null;
    private String lastModified = null;

    private static class TeamConfiguration {
        Map<String, List<String>> membership = null;
        Map<String, List<String>> teamExtension = null;
    }

    private static class ConfigContainer {
        TeamConfiguration data = null;
        String lastModified;
        String lastModifiedBy;
    }

    @Autowired
    public DynamicTeamService(EntitySProcService entityService, ObjectMapper mapper) {
        this.entityService = entityService;
        this.mapper = mapper;
    }

    public Optional<List<String>> getTeams(String userId) {
        if (null == config) {
            return Optional.empty();
        }

        List<String> teams = config.membership.get(userId);
        if (null == teams) {
            return Optional.empty();
        }


        return Optional.of(teams);
    }

    @Scheduled(fixedRate = 60000)
    public void refreshTeamConfig() {
        try {
            doRefersh();
        }
        catch(Throwable t) {
            log.error("Failed to refresh dynamic team configuration from entityId=zmon-team-config", t.getMessage());
        }
    }

    public void doRefersh() throws IOException {
        List<String> entities = entityService.getEntityById("zmon-team-config");
        if (entities.size() != 1) {
            return;
        }

        ConfigContainer container = mapper.readValue(entities.get(0), ConfigContainer.class);
        if (null != container.data && null != container.data.membership && null != container.data.teamExtension) {
            if (!container.lastModified.equals(lastModified)) {
                log.info("Updating team configuration: lastModified={} by={}", container.lastModified, container.lastModifiedBy);
                lastModified = container.lastModified;
                config = container.data;
            }
        }
        else {
            log.warn("Dynamic team configuration not valid! data, membership, and team_extension field must be present!");
        }
    }
}
