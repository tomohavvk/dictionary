package com.tomohavvk.translator.extractor;

import com.tomohavvk.translator.common.commands.TranslateCommand;
import com.tomohavvk.translator.common.models.Split;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordsExtractorImpl implements WordsExtractor {

    private final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build();

    @Override
    public Flux<String> extract(String url, List<String> filterBy, List<Split> splitBy) {
        // TODO init and reuse the client
        var client = WebClient.builder().exchangeStrategies(strategies).baseUrl(url).build();

        return client.get().retrieve().bodyToMono(String.class).map(content -> Arrays.stream(content.split("\n")))
                .map(stream -> stream.filter(content -> filterBy.stream().allMatch(content::contains)))
                .map(stream -> stream.map(content -> split(new ArrayList<>(splitBy), content))).flux()
                .concatMap(stream -> Flux.fromIterable(stream.toList()))
                .doOnError(e -> log.error("error: {}", e.getMessage()));
    }

    private String split(ArrayList<Split> splitBy, String content) {
        if (splitBy.isEmpty())
            return content;
        else {
            var split = splitBy.removeFirst();
            var index = split.isTakeLeft() ? 0 : 1;
            return split(splitBy, content.split(split.by())[index]);
        }
    }
}
