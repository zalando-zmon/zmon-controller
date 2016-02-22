package de.zalando.zmon.service;

import de.zalando.zmon.domain.TrialRunRequest;
import de.zalando.zmon.domain.TrialRunResults;

import java.io.IOException;

public interface TrialRunService {

    String scheduleTrialRun(TrialRunRequest request) throws IOException;

    TrialRunResults getTrialRunResults(String id);
}
