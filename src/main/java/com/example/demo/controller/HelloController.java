package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(Principal principal){
        return "hello "+principal.getName();
    }

    @RequestMapping("/")
    public String redirect(){
        return "redirect";
    }
}
