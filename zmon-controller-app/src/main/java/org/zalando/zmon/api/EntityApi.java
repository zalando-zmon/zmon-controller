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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.persistence.EntitySProcService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
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
        try {
            String data = mapper.writeValueAsString(entity);
            entitySprocs.createOrUpdateEntity(data, Lists.newArrayList(authService.getTeams()), authService.getUserName());
        } catch (IOException ex) {
            log.error("Entity not serializable", ex);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public void getEntities(@RequestParam(value = "query", defaultValue = "[{}]") String data, final Writer writer,
                            final HttpServletResponse response) {
        if (data.startsWith("{")) {
            data = "[" + data + "]";
        }
        List<String> entities = entitySprocs.getEntities(data);

        response.setContentType("application/json");
        try {
            writer.write("[");
            boolean first = true;
            for (String s : entities) {
                if (!first) {
                    writer.write(",");
                }
                first = false;
                writer.write(s);
            }
            writer.write("]");
        } catch (IOException ex) {
            log.error("", ex);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}/")
    public void getEntity(@PathVariable(value = "id") String id, final Writer writer) {
        List<String> entities = entitySprocs.getEntityById(id);
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
    @RequestMapping(value = "/{id}/", method = RequestMethod.DELETE)
    public int deleteEntity(@PathVariable(value = "id") String id) {
        List<String> teams = Lists.newArrayList(authService.getTeams());
        log.info("Deleting entity {} from user {} with teams {}", id, authService.getUserName(), teams);
        List<String> ids = entitySprocs.deleteEntity(id, teams, authService.getUserName());
        return ids.size();
    }

}
