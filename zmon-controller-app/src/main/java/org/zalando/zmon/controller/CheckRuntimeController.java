package org.zalando.zmon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zalando.zmon.config.CheckRuntimeConfig;
import org.zalando.zmon.domain.DefinitionRuntime;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "/rest")
public class CheckRuntimeController extends AbstractZMonController {
    private CheckRuntimeConfig config;

    public CheckRuntimeController(CheckRuntimeConfig config) {
        this.config = config;
    }

    public class CheckRuntimeConfigDto extends CheckRuntimeConfig {
        public Map<DefinitionRuntime, String> getRuntimeLabels() {
            return Stream.of(DefinitionRuntime.values())
                    .collect(Collectors.toMap(Function.identity(), DefinitionRuntime::getLabel));
        }
    }

    @RequestMapping(value = "/checkRuntimeConfig")
    public ResponseEntity<CheckRuntimeConfigDto> getCheckRuntimeConfig() {
        CheckRuntimeConfigDto dto = new CheckRuntimeConfigDto();
        dto.setEnabled(config.isEnabled());
        dto.setDefaultRuntime(config.getDefaultRuntime());
        dto.setAllowedRuntimesForCreate(config.getAllowedRuntimesForCreate());
        dto.setAllowedRuntimesForUpdate(config.getAllowedRuntimesForUpdate());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
