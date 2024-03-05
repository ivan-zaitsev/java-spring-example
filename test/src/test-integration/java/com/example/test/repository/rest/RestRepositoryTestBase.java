package com.example.test.repository.rest;

import com.example.test.service.json.JsonConverterJackson;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RestClientTest
@AutoConfigureMockRestServiceServer(enabled = false)
@Import({ JsonConverterJackson.class })
public class RestRepositoryTestBase {

}
