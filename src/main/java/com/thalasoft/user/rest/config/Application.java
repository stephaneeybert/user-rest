package com.thalasoft.user.rest.config;

import java.util.Arrays;

import com.thalasoft.toolbox.condition.EnvProd;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@EnvProd
@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Application started with command-line arguments: {} .\nTo kill this application, press Ctrl + C.",
        Arrays.toString(args));
    if (null != args && args.length > 0) {
    }
  }

}