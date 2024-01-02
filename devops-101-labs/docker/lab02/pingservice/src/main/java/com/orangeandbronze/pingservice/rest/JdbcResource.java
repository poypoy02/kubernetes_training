package com.orangeandbronze.pingservice.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/connecttodb")
public class JdbcResource {

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @GetMapping()
    public String connectToDB() {
        return "This emulates a DB connection, in this example we connect to: " + jdbcUrl;
    }
}
