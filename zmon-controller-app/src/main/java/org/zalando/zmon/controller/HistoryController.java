package org.zalando.zmon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.zmon.domain.Activity;
import org.zalando.zmon.domain.ActivityDiff;
import org.zalando.zmon.domain.HistoryAction;
import org.zalando.zmon.domain.RestoreCheckDefinitionRequest;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.HistoryService;

@Controller
@RequestMapping(value="/rest")
public class HistoryController extends AbstractZMonController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private DefaultZMonPermissionService authorityService;

    @RequestMapping(value = "/alertHistory", method = RequestMethod.GET)
    public ResponseEntity<List<Activity>> getHistory(
            @RequestParam(value = "alert_definition_id", required = true) final int alertDefinitionId,
            @RequestParam(value = "limit", required = false) final Integer limit,
            @RequestParam(value = "from", required = false) final Long from,
            @RequestParam(value = "to", required = false) final Long to) {

        return new ResponseEntity<>(historyService.getHistory(alertDefinitionId, limit, from, to), HttpStatus.OK);
    }

    @RequestMapping(value = "/alertDefinitionHistory", method = RequestMethod.GET)
    public ResponseEntity<List<ActivityDiff>> getAlertDefinitionHistory(
            @RequestParam(value = "alert_definition_id", required = true) final int alertDefinitionId,
            @RequestParam(value = "limit", required = false) final Integer limit,
            @RequestParam(value = "from", required = false) final Long from,
            @RequestParam(value = "to", required = false) final Long to) {

        return new ResponseEntity<>(historyService.getAlertDefinitionHistory(alertDefinitionId, limit, from, to),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/checkDefinitionHistory", method = RequestMethod.GET)
    public ResponseEntity<List<ActivityDiff>> getCheckDefinitionHistory(
            @RequestParam(value = "check_definition_id", required = true) final int checkDefinitionId,
            @RequestParam(value = "limit", required = false) final Integer limit,
            @RequestParam(value = "from", required = false) final Long from,
            @RequestParam(value = "to", required = false) final Long to,
            @RequestParam(value = "action", required = false) final HistoryAction action) {

        return new ResponseEntity<>(historyService.getCheckDefinitionHistory(checkDefinitionId, limit, from, to, action),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/restoreCheckDefinition", method = RequestMethod.POST)
    public ResponseEntity<Boolean> restoreCheckDefinition(
        @RequestBody(required = true) RestoreCheckDefinitionRequest request
    ) {
        return new ResponseEntity<>(historyService.restoreCheckDefinition(request.getCheckDefinitionHistoryId()), HttpStatus.OK);
    }
}
