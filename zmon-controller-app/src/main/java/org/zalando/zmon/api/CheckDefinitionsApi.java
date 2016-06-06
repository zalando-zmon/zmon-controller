package org.zalando.zmon.api;

import com.google.common.base.Preconditions;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.controller.AbstractZMonController;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.ZMonService;

import java.util.List;

@Controller
@RequestMapping("/api/v1/check-definitions")
public class CheckDefinitionsApi extends AbstractZMonController {

    private final ZMonService zMonService;

    private final DefaultZMonPermissionService authorityService;

    private static final Logger LOG = LoggerFactory.getLogger(CheckDefinitionsApi.class);

    @Autowired
    public CheckDefinitionsApi(final ZMonService zMonService, final DefaultZMonPermissionService authorityService) {
        this.zMonService = Preconditions.checkNotNull(zMonService, "zMonService is null");
        this.authorityService = Preconditions.checkNotNull(authorityService, "authorityService is null");
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CheckDefinition createOrUpdate(@Valid
            @RequestBody(required = true)
            final CheckDefinitionImport checkDefinition) throws ZMonException {

        return zMonService.createOrUpdateCheckDefinition(checkDefinition);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CheckDefinition createOrUpdateById(@Valid
                                          @RequestBody(required = true)
                                          final CheckDefinitionImport checkDefinition, @PathVariable(value = "id") int id) throws ZMonException {

        if (null == checkDefinition.getId()) {
            throw new ZMonException("ID missing in body");
        }

        if (id != checkDefinition.getId()) {
            throw new ZMonException("ID from path does not match the one received in body");
        }

        return zMonService.createOrUpdateCheckDefinition(checkDefinition);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CheckDefinitionImport getCheckDef(@PathVariable(value = "id") int id) throws ZMonException {
        List<CheckDefinition> list = zMonService.getCheckDefinitionsById(id);
        CheckDefinition check = list.get(0);
        if(check==null) return null;

        CheckDefinitionImport cImport = new CheckDefinitionImport();

        cImport.setId(check.getId());
        cImport.setName(check.getName());
        cImport.setDescription(check.getDescription());
        cImport.setInterval(check.getInterval());
        cImport.setCommand(check.getCommand());
        cImport.setEntities(check.getEntities());
        cImport.setOwningTeam(check.getOwningTeam());
        cImport.setLastModifiedBy(check.getLastModifiedBy());
        cImport.setSourceUrl(check.getSourceUrl());
        cImport.setStatus(check.getStatus());

        cImport.setTechnicalDetails(check.getTechnicalDetails());
        cImport.setPotentialAnalysis(check.getPotentialAnalysis());
        cImport.setPotentialImpact(check.getPotentialImpact());

        return cImport;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<String> deleteUnusedCheck(@PathVariable(value = "id") int id) throws ZMonException {
        LOG.info("Deleting unused check id={} user={} teams={}", id, authorityService.getUserName(), authorityService.getTeams());

        List<Integer> ids = zMonService.deleteUnusedCheckDef(id, authorityService.getTeams());
        if(ids.size() == 1) {
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Id not found or not part of owning team", HttpStatus.NOT_FOUND);
    }
}
