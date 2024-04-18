package com.tomohavvk.dictionary.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@PropertySource("classpath:application.properties")
@PropertySource("classpath:persistence.properties")
@SpringBootApplication(scanBasePackages = "com.tomohavvk.dictionary")
public class DictionaryLauncher {

    public static void main(String[] args) {
        SpringApplication.run(DictionaryLauncher.class, args);
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
}
