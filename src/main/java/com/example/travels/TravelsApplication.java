package com.example.travels;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class TravelsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TravelsApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("Best Travel Application");
    }
}
