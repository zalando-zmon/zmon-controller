package org.zalando.zmon.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.domain.AlertDefinitions;
import org.zalando.zmon.domain.AlertDefinitionsDiff;
import org.zalando.zmon.domain.CheckDefinitions;
import org.zalando.zmon.domain.CheckDefinitionsDiff;
import org.zalando.zmon.webservice.service.ZMonWebService;

/**
 * Created by jmussler on 3/10/15.
 */
@Controller
@RequestMapping("/api/v1/checks")
public class CheckAPI {
    @Autowired
    ZMonWebService service;

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/all-check-definitions", method = RequestMethod.GET)
    public CheckDefinitions getAllCheckDefinitions() {
        return service.getAllCheckDefinitions();
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/all-active-check-definitions", method = RequestMethod.GET)
    public CheckDefinitions getAllActiveCheckDefinitions() {
        return service.getAllActiveCheckDefinitions();
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/check-definitions-diff", method = RequestMethod.GET)
    public CheckDefinitionsDiff getCheckDefinitionsDiff(@RequestParam(value = "lastSnapshotId") Long snapshotId) {
        return service.getCheckDefinitionsDiff(snapshotId);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/all-active-alert-definitions", method = RequestMethod.GET)
    public AlertDefinitions getAllActiveAlertDefinitions() {
        return service.getAllActiveAlertDefinitions();
    }


    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/alert-definitions-diff", method = RequestMethod.GET)
    public AlertDefinitionsDiff getAlertDefinitionsDiff(@RequestParam(value = "lastSnapshotId") Long snapshotId) {
        return service.getAlertDefinitionsDiff(snapshotId);
    }
}
