package com.tomohavvk.dictionary.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = "com.tomohavvk.dictionary")
@PropertySource("classpath:application.properties")
@PropertySource("classpath:persistence.properties")
@EnableR2dbcRepositories(basePackages = { "com.tomohavvk.dictionary.persistence" })
public class DictionaryLauncher {

    public static void main(String[] args) {
        SpringApplication.run(DictionaryLauncher.class, args);
    }
}
