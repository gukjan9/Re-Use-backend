package com.example.demo;

import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private final String port;

    private HomeController(@Value("${server.port}") final String port){
        this.port = port;
    }

    @GetMapping("/port")
    public String port(){
        return port;
    }
}
