package org.zalando.zmon.webservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.service.ZMonService;

import javax.validation.Validator;

// TODO check validation
// TODO handle errors
// TODO move interface to a new module
@Component(ZMonWebServiceImpl.BEAN_NAME)
public class ZMonWebServiceImpl implements ZMonWebService {

    public static final String BEAN_NAME = "zMonWebService";

    @Autowired
    private ZMonService zMonService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private Validator validator;

    @Override
    public CheckDefinitions getAllCheckDefinitions() {
        return zMonService.getCheckDefinitions(null);
    }

    @Override
    public CheckDefinitions getAllActiveCheckDefinitions() {
        return zMonService.getCheckDefinitions(DefinitionStatus.ACTIVE);
    }

    @Override
    public CheckDefinitionsDiff getCheckDefinitionsDiff(final Long snapshotId) {
        return zMonService.getCheckDefinitionsDiff(snapshotId);
    }

    @Override
    public AlertDefinitions getAllActiveAlertDefinitions() {
        return alertService.getActiveAlertDefinitionsDiff();
    }

    @Override
    public AlertDefinitionsDiff getAlertDefinitionsDiff(final Long snapshotId) {
        return alertService.getAlertDefinitionsDiff(snapshotId);
    }

}
