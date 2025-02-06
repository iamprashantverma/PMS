package com.pms.projectservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core")
public class ProjectController {

    @GetMapping
    public void hello(){
        System.out.println("sb sahi hai bro");
    }
}
