package com.example.test.controller.rest.endpoint;

import com.example.test.config.rest.WebSecurityConfig;
import com.example.test.service.json.JsonConverterJackson;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import({
    JsonConverterJackson.class,
    WebSecurityConfig.class })
public class ControllerMvcTestBase {

}
