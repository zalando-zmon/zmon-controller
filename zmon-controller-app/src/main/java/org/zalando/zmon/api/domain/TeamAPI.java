package org.zalando.zmon.api.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.service.ZMonService;

import java.util.List;

/**
 * Created by jmussler on 12.07.16.
 */
@Controller
@RequestMapping(path="/api/v1/teams")
public class TeamAPI {

    @Autowired
    ZMonService service;

    @RequestMapping(value = "")
    public ResponseEntity<List<String>> getAllTeams() {
        return new ResponseEntity<>(service.getAllTeams(), HttpStatus.OK);
    }
}
