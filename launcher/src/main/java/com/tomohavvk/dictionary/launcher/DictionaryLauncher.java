package com.tomohavvk.dictionary.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.tomohavvk.dictionary")
public class DictionaryLauncher {

    public static void main(String[] args) {
        SpringApplication.run(DictionaryLauncher.class, args);
    }
}
