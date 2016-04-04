package org.zalando.zmon.service;

import java.io.IOException;

import org.zalando.zmon.domain.TrialRunRequest;
import org.zalando.zmon.domain.TrialRunResults;

public interface TrialRunService {

    String scheduleTrialRun(TrialRunRequest request) throws IOException;

    TrialRunResults getTrialRunResults(String id);
}
