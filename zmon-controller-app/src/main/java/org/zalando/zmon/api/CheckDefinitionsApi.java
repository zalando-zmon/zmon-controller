package org.zalando.zmon.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
import org.zalando.zmon.persistence.CheckDefinitionImportResult;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.ZMonService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @RequestMapping(method = RequestMethod.HEAD)
    public void getMaxLastModified(HttpServletResponse response) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        df.setTimeZone(tz);

        Date date = zMonService.getMaxCheckDefinitionLastModified();
        response.setHeader("Last-Modified", df.format(date));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<CheckDefinition> createOrUpdate(@Valid
                                          @RequestBody(required = true)
                                          final CheckDefinitionImport checkDefinition) throws ZMonException {

        CheckDefinitionImportResult result = zMonService.createOrUpdateCheckDefinition(checkDefinition, authorityService.getUserName(), Lists.newArrayList(authorityService.getTeams()), authorityService.hasAdminAuthority());

        if (result.isPermissionDenied()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(result.getEntity(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<CheckDefinition> createOrUpdateById(@Valid
                                              @RequestBody(required = true)
                                              final CheckDefinitionImport checkDefinition, @PathVariable(value = "id") int id) throws ZMonException {

        if (null == checkDefinition.getId()) {
            throw new ZMonException("ID missing in body");
        }

        if (id != checkDefinition.getId()) {
            throw new ZMonException("ID from path does not match the one received in body");
        }

        return createOrUpdate(checkDefinition);
    }

    @RequestMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CheckDefinitionImport getCheckDef(@PathVariable(value = "id") int id) throws ZMonException {
        Optional<CheckDefinition> list = zMonService.getCheckDefinitionById(id);
        if (!list.isPresent()) {
            return null;
        }
        CheckDefinition check = list.get();

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
        cImport.setPotentialSolution(check.getPotentialSolution());

        return cImport;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<String> deleteUnusedCheck(@PathVariable(value = "id") int id) throws ZMonException {
        LOG.info("Deleting unused check id={} user={} teams={}", id, authorityService.getUserName(), authorityService.getTeams());
        authorityService.verifyDeleteUnusedCheckDefinitionPermission(id);

        List<Integer> ids = zMonService.deleteUnusedCheckDef(id);
        if (ids.size() == 1) {
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Id not found or not part of owning team", HttpStatus.NOT_FOUND);
    }
}
