package de.zalando.zmon.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.zmon.exception.ZMonRuntimeException;
import de.zalando.zmon.persistence.EntitySProcService;
import de.zalando.zmon.security.ZMonAuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Created by jmussler on 1/28/15.
 */
@Controller
@RequestMapping("/api/v1/entities")
public class EntityApi {

    private static final Logger LOG = LoggerFactory.getLogger(EntityApi.class);

    @Autowired
    EntitySProcService entitySprocs;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ZMonAuthorityService authService;

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public void addEntity(@RequestBody Map<String,String> entity) {
        if(!entity.containsKey("team") || !authService.getTeams().contains(entity.get("team"))) {
            throw new ZMonRuntimeException("Entity Team does not match any of your Teams! " + authService.getTeams());
        }

        try {
            String data = mapper.writeValueAsString(entity);
            entitySprocs.createOrUpdateEntity(data, "", authService.getUserName());
        }
        catch(IOException ex) {
            LOG.error("Entity not serializable", ex);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void getEntities(@RequestParam(value = "query", defaultValue = "{}") String data, final Writer writer,
                            final HttpServletResponse response) {
        List<String> entities = entitySprocs.getEntities(data);

        response.setContentType("application/json");
        try {
            writer.write("[");
            boolean first = true;
            for(String s : entities) {
                if(!first) {
                    writer.write(",");
                }
                first = false;
                writer.write(s);
            }
            writer.write("]");
        }
        catch(IOException ex) {

        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void getEntity(@PathVariable(value = "id") String id, final Writer writer,
                          final HttpServletResponse response) {
        List<String> entities = entitySprocs.getEntityById(id);
        try {
            for (String s : entities) {
                writer.write(s);// there is at most one entity
            }
        }
        catch(IOException ex) {

        }
    }
}
