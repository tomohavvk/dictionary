package com.tomohavvk.dictionary.parser;

import com.tomohavvk.dictionary.common.models.ExtractCommand;
import com.tomohavvk.dictionary.persistence.entities.SourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.LinkedList;

@Component
@RequiredArgsConstructor
public class WordsParser {

    private final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build();

    public Flux<SourceEntity> parse(ExtractCommand command) {
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
                })).flux().concatMap(stream -> {
                    var sources = stream.map(word -> new SourceEntity(0L, word, command.sourceLanguage())).toList();

                    return Flux.fromIterable(sources);
                });
    }
}
