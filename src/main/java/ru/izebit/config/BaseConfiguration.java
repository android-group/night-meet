package ru.izebit.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ru.izebit")
public class BaseConfiguration {

    static class Profile {
        static final String DEVELOPMENT = "development";
        static final String TEST = "test";
    }
}
