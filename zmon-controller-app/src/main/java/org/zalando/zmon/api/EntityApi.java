package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.api.domain.EntityObject;
import org.zalando.zmon.api.domain.ResourceNotFoundException;
import org.zalando.zmon.persistence.EntitySProcService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmussler on 1/28/15.
 */
@Controller
@RequestMapping("/api/v1/entities")
public class EntityApi {

    private final Logger log = LoggerFactory.getLogger(EntityApi.class);

    EntitySProcService entitySprocs;

    ObjectMapper mapper;

    DefaultZMonPermissionService authService;

    @Autowired
    public EntityApi(EntitySProcService entitySprocs, ObjectMapper mapper, DefaultZMonPermissionService authService) {
        this.entitySprocs = entitySprocs;
        this.mapper = mapper;
        this.authService = authService;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleCheckConstraintViolation(DataIntegrityViolationException exception) {
        if (exception.getCause() instanceof PSQLException && exception.getMessage().contains("violates check constraint")) {
            return new ResponseEntity<>("Check constraint violated", HttpStatus.BAD_REQUEST);
        }
        throw exception;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/", ""}, method = {RequestMethod.PUT, RequestMethod.POST})
    public void addEntity(@RequestBody JsonNode entity) {
        if (entity.has("type") && "zmon_config".equals(entity.get("type").textValue())) {
            if (!authService.hasAdminAuthority()) {
                throw new AccessDeniedException("No ADMIN privileges present to update configuration.");
            }
            log.info("Modifying config entity: id={} user={}", entity.get("id"), authService.getUserName());
        }

        try {
            String data = mapper.writeValueAsString(entity);
            String id = entitySprocs.createOrUpdateEntity(data, Lists.newArrayList(authService.getTeams()), authService.getUserName());
            if (id == null) {
                throw new AccessDeniedException("Access denied: entity was not updated");
            }
        } catch (IOException ex) {
            log.error("Entity not serializable", ex);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public List<EntityObject> getEntities(@RequestParam(value = "query", defaultValue = "[{}]") String data) throws IOException {

        if (data.startsWith("{")) {
            data = "[" + data + "]";
        }

        List<String> entitiesString = entitySprocs.getEntities(data);
        List<EntityObject> list = new ArrayList<>(entitiesString.size());

        for(String e : entitiesString) {
            EntityObject o = mapper.readValue(e, EntityObject.class);
            list.add(o);
        }

        return list;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/{id}/", "/{id}"})
    public void getEntity(@PathVariable(value = "id") String id, final Writer writer) {
        List<String> entities = entitySprocs.getEntityById(id);
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        try {
            for (String s : entities) {
                writer.write(s);// there is at most one entity
            }
        } catch (IOException ex) {
            log.error("", ex);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/{id}/", "/{id}"}, method = RequestMethod.DELETE)
    public int deleteEntity(@PathVariable(value = "id") String id) {
        List<String> teams = Lists.newArrayList(authService.getTeams());
        log.info("Deleting entity {} from user {} with teams {}", id, authService.getUserName(), teams);
        List<String> ids = entitySprocs.deleteEntity(id, teams, authService.getUserName());
        return ids.size();
    }

}
