package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zalando.zmon.controller.CheckRuntimeController;
import org.zalando.zmon.controller.domain.FrontendBootData;
import org.zalando.zmon.domain.SumMinuteChecks;
import org.zalando.zmon.persistence.EntitySProcService;
import org.zalando.zmon.service.FrontendBootDataService;

import java.io.IOException;
import java.util.List;

@Service
public class FrontendBootDataServiceImpl implements FrontendBootDataService {
    private final Logger log = LoggerFactory.getLogger(CheckRuntimeController.class);
    private String lastModifiedForSumMinuteChecks = null;
    private final EntitySProcService entityService;
    private final ObjectMapper mapper;
    private List<Integer> subMinuteChecks = null;

    @Autowired
    public FrontendBootDataServiceImpl(EntitySProcService entityService, ObjectMapper mapper) {
        this.entityService = entityService;
        this.mapper = mapper;
    }

    @Override
    public FrontendBootData getFrontendBootData() {
        FrontendBootData data = new FrontendBootData();
        data.setSubMinuteChecks(subMinuteChecks);

        return null;
    }

    @Scheduled(fixedRate = 60_000)
    public void refreshSubMinuteChecks() {
        try {
            doRefresh();
        }
        catch(Throwable t) {
            log.error("Failed to refresh whitelisted sub-minute checks from entityId=zmon-sub-minute-checks", t.getMessage());
        }
    }

    private void doRefresh() throws IOException {
        List<String> entities = entityService.getEntities("[{\"type\":\"zmon_config\", \"id\":\"zmon-sub-minute-checks\"}]");
        if (entities.size() != 1) {
            return;
        }

        SumMinuteChecks config = mapper.readValue(entities.get(0), SumMinuteChecks.class);
        if (null != config.data) {
            if (!config.lastModified.equals(lastModifiedForSumMinuteChecks)) {
                log.info("Updating whitelisted sub-minute checks: lastModifiedForSumMinuteChecks={} by={}", config.lastModified, config.lastModifiedBy);
                lastModifiedForSumMinuteChecks = config.lastModified;
                subMinuteChecks = config.data;
            }
        }
        else {
            log.warn("zmon-sub-minute-checks is empty!");
        }
    }
}
