package com.tomohavvk.dictionary.parser;

import com.tomohavvk.dictionary.common.models.ParseCommand;
import com.tomohavvk.dictionary.persistence.TranslateRepository;
import com.tomohavvk.dictionary.persistence.entities.SourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedList;

@Component
@RequiredArgsConstructor
public class WordsParser {

    private final TranslateRepository translateRepository;

    private final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build();

    public Mono<Long> parse(ParseCommand command) {
        var client = WebClient.builder().exchangeStrategies(strategies).baseUrl(command.url()).build();

        return client.get().retrieve().bodyToMono(String.class).map(content -> Arrays.stream(content.split("\n")))
                .map(stream -> stream.filter(content -> command.filterBy().stream().allMatch(content::contains)))
                .map(stream -> stream.map(content -> {
                    String res = content;
                    for (var split : command.splitBy()) {
                        var index = split.isTakeLeft() ? 0 : 1;
                        res = res.split(split.by())[index];
                    }
                    return res;
                })).flatMap(stream -> {
                    var sources = new LinkedList<>(stream.map(word -> new SourceEntity(0L, word, "en")).toList());

                    return translateRepository.upsertSources(sources).reduce(Long::sum);
                });
    }
}
