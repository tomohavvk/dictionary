package com.tomohavvk.translator.web;

import com.tomohavvk.translator.common.commands.LoadTranslationsCommand;
import com.tomohavvk.translator.common.commands.TranslateCommand;
import com.tomohavvk.translator.services.TranslatorService;
import com.tomohavvk.translator.web.exceptions.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handlers {

    private final TranslatorService translateService;

    public Mono<ServerResponse> translate(ServerRequest request) {
        return request.bodyToMono(TranslateCommand.class)
                .flatMap(command -> translateService.translate(command).flatMap(wordsCount -> ServerResponse.ok()
                        .body(BodyInserters.fromValue(Map.of("words_count", wordsCount)))));
    }

    public Mono<ServerResponse> load(ServerRequest request) {
        return makeLoadCommand(request).flatMap(command -> translateService.loadTranslations(command).collectList()
                .flatMap(targets -> ServerResponse.ok().body(BodyInserters.fromValue(targets))));
    }

    private Mono<LoadTranslationsCommand> makeLoadCommand(ServerRequest request) {
        var sourceLanguage = request.queryParam("sourceLanguage");
        var targetLanguage = request.queryParam("targetLanguage");

        if (sourceLanguage.isEmpty())
            return Mono.error(new InvalidRequestException("missing required query parameter: sourceLanguage"));
        if (targetLanguage.isEmpty())
            return Mono.error(new InvalidRequestException("missing required query parameter: targetLanguage"));
        else
            try {
                var limit = request.queryParam("limit").map(Integer::valueOf).orElse(100);
                var offset = request.queryParam("offset").map(Integer::valueOf).orElse(0);

                return Mono
                        .just(new LoadTranslationsCommand(sourceLanguage.get(), targetLanguage.get(), limit, offset));
            } catch (NumberFormatException e) {
                return Mono.error(new InvalidRequestException(
                        String.format("unable to parse `limit` or `offset` query parameter: %s", e.getMessage())));
            }
    }

}