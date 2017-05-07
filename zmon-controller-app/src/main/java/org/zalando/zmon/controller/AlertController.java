package org.zalando.zmon.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.AlertService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/rest")
public class AlertController extends AbstractZMonController {

    @Autowired
    private AlertService service;

    @Autowired
    private DefaultZMonPermissionService authorityService;

    @RequestMapping(value = "/allAlerts", method = RequestMethod.GET)
    public ResponseEntity<List<Alert>> getAllAlerts(
            @RequestParam(value = "team", required = false) final Set<String> teams,
            @RequestParam(value = "tags", required = false) final Set<String> tags) {
        final List<Alert> alerts = teams == null && tags == null ? service.getAllAlerts()
                : service.getAllAlertsByTeamAndTag(teams, tags);

        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @RequestMapping(value = "/alertsById")
    public ResponseEntity<List<Alert>> getAlertsById(
            @RequestParam(value = "id", required = true) final Set<Integer> ids) {

        return new ResponseEntity<>(service.getAllAlertsById(ids), HttpStatus.OK);
    }

    @RequestMapping(value = "/alertDetails")
    public ResponseEntity<Alert> getAlert(@RequestParam(value = "alert_id", required = true) final Integer alertId) {
        final Alert alert = service.getAlert(alertId);

        return alert == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(alert, HttpStatus.OK);
    }

    @RequestMapping(value = "/alertDefinitions")
    public ResponseEntity<List<AlertDefinitionAuth>> getAllAlertDefinitions(
            @RequestParam(value = "team", required = false) final Set<String> teams,
            @RequestParam(value = "check_id", required = false) final Integer checkId) {
        List<AlertDefinitionAuth> response = Collections.emptyList();

        final List<AlertDefinition> defs = teams == null ? service.getAllAlertDefinitions()
                : service.getAlertDefinitions(null, teams);

        if (defs != null && !defs.isEmpty()) {
            response = new ArrayList<>(defs.size());
            for (final AlertDefinition def : defs) {
                if (checkId == null || checkId.equals(def.getCheckDefinitionId())) {
                    response.add(resolveAlertDefinitionAuth(def));
                }
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private AlertDefinitionAuth resolveAlertDefinitionAuth(final AlertDefinition def) {
        return AlertDefinitionAuth.from(def, authorityService.hasEditAlertDefinitionPermission(def),
                authorityService.hasAddAlertDefinitionPermission(),
                authorityService.hasDeleteAlertDefinitionPermission(def));
    }

    @RequestMapping(value = "/alertDefinition")
    public ResponseEntity<AlertDefinitionAuth> getAlertDefinition(
            @RequestParam(value = "id", required = true) final int id) {

        final List<AlertDefinition> alertDefinitions = service.getAlertDefinitions(null, Lists.newArrayList(id));
        if (alertDefinitions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final AlertDefinition def = alertDefinitions.get(0);
        return new ResponseEntity<>(resolveAlertDefinitionAuth(def), HttpStatus.OK);
    }

    @RequestMapping(value = "/alertDefinitionNode")
    public ResponseEntity<AlertDefinitionAuth> getAlertDefinitionNode(
            @RequestParam(value = "id", required = true) final int id) {

        final AlertDefinition node = service.getAlertDefinitionNode(id);
        if (node == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(resolveAlertDefinitionAuth(node), HttpStatus.OK);
    }

    @RequestMapping(value = "/alertDefinitionChildren")
    public ResponseEntity<List<AlertDefinitionAuth>> getAlertDefinitionChildren(
            @RequestParam(value = "id", required = true) final int id) {

        List<AlertDefinitionAuth> response = Collections.emptyList();
        final List<AlertDefinition> defs = service.getAlertDefinitionChildren(id);

        if (defs != null && !defs.isEmpty()) {
            response = new ArrayList<>(defs.size());
            for (final AlertDefinition def : defs) {
                response.add(resolveAlertDefinitionAuth(def));
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateAlertDefinition", method = RequestMethod.POST)
    public ResponseEntity<AlertDefinitionAuth> updateAlertDefinition(
            @Valid
            @RequestBody(required = true)
            final AlertDefinition alertDefinition) throws ZMonException {

        alertDefinition.setLastModifiedBy(authorityService.getUserName());

        // check security
        authorityService.verifyEditAlertDefinitionPermission(alertDefinition);

        return new ResponseEntity<>(resolveAlertDefinitionAuth(service.createOrUpdateAlertDefinition(alertDefinition)),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/alertNotificationsAck", method = RequestMethod.PUT)
    public ResponseEntity<Void> ackAlertNotification(
            @RequestParam(value = "alert_id") final int alertId) {
        service.acknowledgeAlert(alertId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteAlertDefinition", method = RequestMethod.DELETE)
    public void deleteAlertDefinition(@RequestParam(value = "id", required = true) final int id) throws ZMonException {
        authorityService.verifyDeleteAlertDefinitionPermission(id);
        service.deleteAlertDefinition(id);
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public ResponseEntity<AlertCommentAuth> addComment(@Valid
                                                       @RequestBody(required = true)
                                                       final AlertComment comment) throws ZMonException {

        authorityService.verifyAddCommentPermission();

        final String currentUser = authorityService.getUserName();
        comment.setCreatedBy(currentUser);
        comment.setLastModifiedBy(currentUser);

        return new ResponseEntity<>(AlertCommentAuth.from(service.addComment(comment),
                authorityService.hasDeleteCommentPermission(comment)), HttpStatus.OK);
    }

    @RequestMapping(value = "/comments")
    public ResponseEntity<List<AlertCommentAuth>> getComments(
            @RequestParam(value = "alert_definition_id", required = true) final int alertDefinitionId,
            @RequestParam(value = "limit", defaultValue = "20") final int limit,
            @RequestParam(value = "offset", defaultValue = "0") final int offset) {

        final List<AlertComment> currentComments = service.getComments(alertDefinitionId, limit, offset);

        final List<AlertCommentAuth> comments = currentComments.stream().map(
                comment -> AlertCommentAuth.from(comment, authorityService.hasDeleteCommentPermission(comment))).collect(Collectors.toList());

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteComment", method = RequestMethod.DELETE)
    public void deleteComment(@RequestParam(value = "id", required = true) final int id) throws ZMonException {
        authorityService.verifyDeleteCommentPermission(id);
        service.deleteAlertComment(id);
    }

    @ResponseBody
    @RequestMapping(value = "/forceAlertEvaluation", method = RequestMethod.POST)
    public void forceAlertEvaluation(@Valid @RequestBody final InstantaneousAlertEvaluationRequest request) throws IOException {
        authorityService.verifyInstantaneousAlertEvaluationPermission();
        service.forceAlertEvaluation(request.getAlertDefinitionId());
    }

    @RequestMapping(value = "/allTags")
    public ResponseEntity<List<String>> getAllTags() {
        return new ResponseEntity<>(service.getAllTags(), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "cleanAlertState", method = RequestMethod.POST)
    public void cleanAlertState(@Valid @RequestBody final InstantaneousAlertEvaluationRequest request) {
        authorityService.verifyInstantaneousAlertEvaluationPermission();
        service.cleanAlertState(request.getAlertDefinitionId());
    }
}
