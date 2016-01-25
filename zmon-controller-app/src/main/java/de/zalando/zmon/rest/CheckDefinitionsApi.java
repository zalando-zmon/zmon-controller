package de.zalando.zmon.rest;

import com.google.common.base.Preconditions;
import de.zalando.zmon.controller.AbstractZMonController;
import de.zalando.zmon.domain.CheckDefinition;
import de.zalando.zmon.domain.CheckDefinitionImport;
import de.zalando.zmon.exception.ZMonException;
import de.zalando.zmon.security.permission.DefaultZMonPermissionService;
import de.zalando.zmon.service.ZMonService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/check-definitions")
public class CheckDefinitionsApi extends AbstractZMonController {

    private final ZMonService zMonService;

    private final DefaultZMonPermissionService authorityService;

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

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCheckDef(@Valid @RequestBody(required = true)
            final CheckDefinitionImport checkDefinition) throws ZMonException {

        zMonService.deleteCheckDefinition(checkDefinition);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CheckDefinitionImport getCheckDef(@PathVariable(value = "id") int id) throws ZMonException {
        List<CheckDefinition> list = zMonService.getCheckDefinitionsById(id);
        CheckDefinition check = list.get(0);
        if(check==null) return null;

        CheckDefinitionImport cImport = new CheckDefinitionImport();

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

}
