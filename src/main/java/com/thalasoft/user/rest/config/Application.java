package com.thalasoft.user.rest.config;

import com.thalasoft.toolbox.condition.EnvProd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnvProd
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
      }
    
}