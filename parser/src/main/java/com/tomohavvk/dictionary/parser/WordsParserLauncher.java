package com.tomohavvk.dictionary.parser;

import com.tomohavvk.dictionary.persistence.AwaitTranslateRepository;
import com.tomohavvk.dictionary.persistence.entities.AwaitTranslateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.LinkedList;

@PropertySource("classpath:persistence.properties")
@SpringBootApplication(scanBasePackages = "com.tomohavvk.dictionary")
@EnableR2dbcRepositories(basePackages = { "com.tomohavvk.dictionary.persistence" })
public class WordsParserLauncher implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(WordsParserLauncher.class, args);
    }

    @Autowired
    private AwaitTranslateRepository awaitTranslateRepository;

    @Override
    public void run(String... args) {
        final int size = 16 * 1024 * 1024;
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size)).build();

        var client = WebClient.builder().exchangeStrategies(strategies).baseUrl(
                "https://www.use-in-a-sentence.com/english-words/10000-words/the-most-frequent-10000-words-of-english.html")
                .build();

        client.get().retrieve().bodyToMono(String.class).blockOptional()
                .map(content -> Arrays.stream(content.split("\n")))
                .map(stream -> stream
                        .filter(content -> content.contains("</a></li>") && content.contains("/10000-words/")))
                .map(stream -> stream.map(content -> content.split("\">")[1].split("</a></li>")[0]))
                .ifPresent(stream -> {
                    var list = new LinkedList<>(stream.map(word -> new AwaitTranslateEntity(0L, word)).toList());

                    System.out.println(awaitTranslateRepository.upsert(list).reduce(Long::sum).block());
                });
    }
}
