package org.zalando.zmon.webservice.service;

import org.zalando.zmon.domain.AlertDefinitions;
import org.zalando.zmon.domain.AlertDefinitionsDiff;
import org.zalando.zmon.domain.CheckDefinitions;
import org.zalando.zmon.domain.CheckDefinitionsDiff;

// TODO create a new module for web services
// TODO create modules: domain, webservice, frontend, service and webservice-client
public interface ZMonWebService {

    CheckDefinitions getAllCheckDefinitions();

    CheckDefinitions getAllActiveCheckDefinitions();

    CheckDefinitionsDiff getCheckDefinitionsDiff(Long snapshotId);

    AlertDefinitions getAllActiveAlertDefinitions();

    AlertDefinitionsDiff getAlertDefinitionsDiff(Long snapshotId);
}
