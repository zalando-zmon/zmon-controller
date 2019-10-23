package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zalando.zmon.controller.CheckRuntimeController;
import org.zalando.zmon.controller.domain.FrontendBootData;
import org.zalando.zmon.domain.MinCheckInterval;
import org.zalando.zmon.persistence.EntitySProcService;
import org.zalando.zmon.service.FrontendBootDataService;

import java.io.IOException;
import java.util.List;

@Service
public class FrontendBootDataServiceImpl implements FrontendBootDataService {
    private final Logger log = LoggerFactory.getLogger(CheckRuntimeController.class);
    private String lastModifiedForMinCheckInterval = null;
    private final EntitySProcService entityService;
    private final ObjectMapper mapper;
    private MinCheckInterval.MinCheckIntervalData minCheckInterval = new MinCheckInterval.MinCheckIntervalData();

    @Autowired
    public FrontendBootDataServiceImpl(EntitySProcService entityService, ObjectMapper mapper) {
        this.entityService = entityService;
        this.mapper = mapper;
    }

    @Override
    public FrontendBootData getFrontendBootData() {
        FrontendBootData data = new FrontendBootData();

        FrontendBootData.MinInterval interval = data.getCheck().getMinInterval();
        interval.setNormal(minCheckInterval.getMinCheckInterval());
        interval.setWhitelisted(minCheckInterval.getMinWhitelistedCheckInterval());
        interval.setWhitelistedChecks(minCheckInterval.getWhitelistedChecks());

        return data;
    }

    @Scheduled(fixedRate = 60_000)
    public void refreshMinCheckInterval() {
        List<String> entities = entityService.getEntities("[{\"type\":\"zmon_config\", \"id\":\"zmon-min-check-interval\"}]");
        if (entities.size() != 1) {
            return;
        }

        try {
            MinCheckInterval config = mapper.readValue(entities.get(0), MinCheckInterval.class);
            if (null != config.getData()) {
                if (!config.getLastModified().equals(lastModifiedForMinCheckInterval)) {
                    log.info("Updating min check interval: lastModifiedForMinCheckInterval={} by={}", config.getLastModified(), config.getLastModifiedBy());
                    lastModifiedForMinCheckInterval = config.getLastModified();
                    minCheckInterval = config.getData();
                }
            } else {
                log.warn("zmon-min-check-interval is empty!");
            }
        } catch (IOException e) {
            log.error("Failed to refresh whitelisted sub-minute checks from entityId=zmon-min-check-interval", e.getMessage());
        }
    }
}
