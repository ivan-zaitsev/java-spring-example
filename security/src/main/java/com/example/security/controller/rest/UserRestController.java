package com.example.security.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserRestController {

    @GetMapping("/api/v1/users")
    public List<Object> findAll() {
        return List.of();
    }

}
