package org.zalando.zmon.api;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
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

import java.lang.Double;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by jmussler on 1/28/15.
 */
@Controller
@RequestMapping("/api/v1/entities")
public class EntityApi {

    private final Logger log = LoggerFactory.getLogger(EntityApi.class);

    private final MetricRegistry metricRegistry;

    EntitySProcService entitySprocs;

    ObjectMapper mapper;

    DefaultZMonPermissionService authService;

    @Autowired
    public EntityApi(EntitySProcService entitySprocs, ObjectMapper mapper, MetricRegistry metricRegistry, DefaultZMonPermissionService authService) {
        this.entitySprocs = entitySprocs;
        this.mapper = mapper;
        this.authService = authService;
        this.metricRegistry = metricRegistry;
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
    public ResponseEntity<String> addEntity(@RequestBody JsonNode entity) {
        Optional<String> maybeNewType = Optional.ofNullable(entity.get("type")).map(JsonNode::textValue);
        Optional<String> maybeId = Optional.ofNullable(entity.get("id")).map(JsonNode::textValue);
        if (maybeNewType.isPresent()) {
            String type = maybeNewType.get();
            if ("global".equals(type.toLowerCase())) {
                return new ResponseEntity<>("Creating entity with type - GLOBAL is not allowed.", HttpStatus.FORBIDDEN);
            }

            if ("zmon_config".equals(type)) {
                if (!authService.hasAdminAuthority()) {
                    throw new AccessDeniedException("No ADMIN privileges present to update configuration.");
                }
                log.info("Modifying config entity: id={} user={}", entity.get("id"), authService.getUserName());
            }
        }

        try {
            Optional<String> maybeOldType = maybeId.map(entitySprocs::getEntityById)
                    .map(List::stream)
                    .flatMap(Stream::findFirst)
                    .flatMap(str -> {
                        try {
                            return Optional.ofNullable(mapper.readTree(str));
                        } catch (IOException e) {
                            return Optional.empty();
                        }
                    })
                    .flatMap(jsonNode -> Optional.ofNullable(jsonNode.get("type")))
                    .map(JsonNode::textValue);
            if (maybeOldType.isPresent() && !maybeOldType.equals(maybeNewType)) {
                return new ResponseEntity<>("Changing the 'type' of an entity is prohibited (entity type implicitly " +
                        "defines the structure of the entity; changes could lead to confusing behavior and/or errors). " +
                        "Please create a new entity instead (with a different id).", HttpStatus.CONFLICT);
            }
            String data = mapper.writeValueAsString(entity);
            String id = entitySprocs.createOrUpdateEntity(data, Lists.newArrayList(authService.getTeams()), authService.getUserName());
            if (id == null) {
                throw new AccessDeniedException("Access denied: entity was not updated");
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Entity not serializable", ex);
            return new ResponseEntity<>("Entity not serializable.", HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public List<EntityObject> getEntities(@RequestParam(value = "query", defaultValue = "[{}]") String data, @RequestParam(value = "exclude", defaultValue = "") String exclude) throws IOException {
        List<String> entitiesString;

        if (!exclude.isEmpty()) {
            entitiesString = entitySprocs.getEntitiesWithoutTag(exclude);
        } else {
            if (data.startsWith("{")) {
                data = "[" + data + "]";
            }

            entitiesString = entitySprocs.getEntities(data);
        }
        List<EntityObject> list = new ArrayList<>(entitiesString.size());

        for (String e : entitiesString) {
            EntityObject o = mapper.readValue(e, EntityObject.class);
            list.add(o);
        }

        return list;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/{id}/", "/{id}"})
    public void getEntity(@PathVariable(value = "id") String id, final Writer writer, final HttpServletResponse response) {
        List<String> entities = entitySprocs.getEntityById(id);
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        try {
            response.setHeader("Content-Type", "application/json; charset=UTF-8");
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
        List<String> deleted = entitySprocs.deleteEntity(id, teams, authService.getUserName());

        if (!deleted.isEmpty()) {
            try {
                JsonNode e = mapper.readValue(deleted.get(0), JsonNode.class);
                String type = e.get("type").textValue().toLowerCase();
                long created = e.get("created").longValue() * 1000;
                long duration = System.currentTimeMillis() - created;
                Timer timer = metricRegistry.timer("controller.entity-lifetime." + type);
                timer.update(duration, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                log.error("", ex);
            }
            log.info("Deleted entity {} by user {} with teams {} => {})", id, authService.getUserName(), teams, deleted.get(0));
        }
        return deleted.size();
    }

}
