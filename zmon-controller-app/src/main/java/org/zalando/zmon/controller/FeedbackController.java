package org.zalando.zmon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zalando.zmon.domain.Feedback;
import org.zalando.zmon.service.FeedbackService;

@Controller
@RequestMapping(value = "/rest")
public class FeedbackController {

    @Autowired
    private FeedbackService service;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping(value = "/feedback", method = RequestMethod.GET)
    public ResponseEntity<ObjectNode> getFeedbackUrl() {
        Feedback feedback = service.getFeedback();
        ObjectNode response = mapper.createObjectNode();

        response.put("url", feedback.getUrl());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
