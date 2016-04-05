package org.zalando.zmon.webservice.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import javax.xml.bind.annotation.XmlElement;

import org.zalando.zmon.domain.AlertDefinitions;
import org.zalando.zmon.domain.AlertDefinitionsDiff;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.CheckDefinitions;
import org.zalando.zmon.domain.CheckDefinitionsDiff;
import org.zalando.zmon.exception.ZMonFault;

// TODO create a new module for web services
// TODO create modules: domain, webservice, frontend, service and webservice-client
@WebService
public interface ZMonWebService {

    @WebMethod
    CheckDefinitions getAllCheckDefinitions();

    @WebMethod
    CheckDefinitions getAllActiveCheckDefinitions();

    @WebMethod
    CheckDefinitionsDiff getCheckDefinitionsDiff(@WebParam(name = "lastSnapshotId") Long snapshotId);

    @WebMethod
    AlertDefinitions getAllActiveAlertDefinitions();

    @WebMethod
    AlertDefinitionsDiff getAlertDefinitionsDiff(@WebParam(name = "lastSnapshotId") Long snapshotId);

    @WebMethod
    void createOrUpdateCheckDefinition(
            @XmlElement(required = true)
            @WebParam(name = "checkDefinition")
            CheckDefinitionImport checkDefinition) throws ZMonFault;

    @WebMethod
    void deleteCheckDefinition(@XmlElement(required = true)
            @WebParam(name = "name")
            String name,
            @XmlElement(required = true)
            @WebParam(name = "owningTeam")
            String owningTeam) throws ZMonFault;

}
