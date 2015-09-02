package io.vertx.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        // This is basically the same example as the web-examples staticsite example but it's booted using
        // SpringBoot, not Vert.x
        SpringApplication.run(Application.class, args);
    }

}
