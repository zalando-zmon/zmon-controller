package org.zalando.zmon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.config.CheckRuntimeConfig;
import org.zalando.zmon.controller.domain.CheckRuntimeConfigDto;

@Controller
@RequestMapping(value = "/rest")
public class CheckRuntimeController extends AbstractZMonController {
    private CheckRuntimeConfig config;

    public CheckRuntimeController(CheckRuntimeConfig config) {
        this.config = config;
    }

    @RequestMapping(value = "/checkRuntimeConfig")
    public ResponseEntity<CheckRuntimeConfigDto> getCheckRuntimeConfig() {
        CheckRuntimeConfigDto dto = CheckRuntimeConfigDto.createFromCheckRuntimeConfig(config);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
