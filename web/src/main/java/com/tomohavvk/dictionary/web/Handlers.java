package com.tomohavvk.dictionary.web;

import com.tomohavvk.dictionary.common.models.ParseCommand;
import com.tomohavvk.dictionary.common.models.TranslateCommand;
import com.tomohavvk.dictionary.service.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handlers {

    private final TranslateService translateService;

    public Mono<ServerResponse> parse(ServerRequest request) {
        return request.bodyToMono(ParseCommand.class).flatMap(command -> translateService.parse(command)
                .flatMap(rowsProcessed -> ServerResponse.ok().body(BodyInserters.fromValue(rowsProcessed))));
    }

    public Mono<ServerResponse> translate(ServerRequest request) {
        return request.bodyToMono(TranslateCommand.class).flatMap(command -> translateService.translate(command)
                .flatMap(rowsProcessed -> ServerResponse.ok().body(BodyInserters.fromValue(rowsProcessed))));
    }

}