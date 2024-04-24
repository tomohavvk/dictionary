package com.tomohavvk.dictionary.web;

import com.tomohavvk.dictionary.common.exceptions.AppException;
import com.tomohavvk.dictionary.common.models.ExtractCommand;
import com.tomohavvk.dictionary.common.models.LoadCommand;
import com.tomohavvk.dictionary.common.models.TransformCommand;
import com.tomohavvk.dictionary.service.TranslateETLService;
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

    private final TranslateETLService translateService;

    public Mono<ServerResponse> extract(ServerRequest request) {
        return request.bodyToMono(ExtractCommand.class)
                .flatMap(command -> translateService.extract(command).flatMap(rowsExtracted -> ServerResponse.ok()
                        .body(BodyInserters.fromValue(Map.of("rows_extracted", rowsExtracted)))));
    }

    public Mono<ServerResponse> transform(ServerRequest request) {
        return request.bodyToMono(TransformCommand.class)
                .flatMap(command -> translateService.transform(command).flatMap(rowsTransformed -> ServerResponse.ok()
                        .body(BodyInserters.fromValue(Map.of("rows_transformed", rowsTransformed)))));
    }

    public Mono<ServerResponse> load(ServerRequest request) {
        return makeLoadCommand(request).flatMap(command -> translateService.load(command).collectList()
                .flatMap(targets -> ServerResponse.ok().body(BodyInserters.fromValue(targets))));
    }

    private Mono<LoadCommand> makeLoadCommand(ServerRequest request) {
        var sourceLanguage = request.queryParam("sourceLanguage");
        var targetLanguage = request.queryParam("targetLanguage");

        if (sourceLanguage.isEmpty())
            return Mono.error(new AppException("missing required query parameter: sourceLanguage"));
        if (targetLanguage.isEmpty())
            return Mono.error(new AppException("missing required query parameter: targetLanguage"));
        else
            try {
                var limit = request.queryParam("limit").map(Integer::valueOf).orElse(100);
                var offset = request.queryParam("offset").map(Integer::valueOf).orElse(0);

                return Mono.just(new LoadCommand(sourceLanguage.get(), targetLanguage.get(), limit, offset));
            } catch (NumberFormatException e) {
                return Mono.error(new AppException(
                        String.format("unable to parse `limit` or `offset` query parameter: %s", e.getMessage())));
            }
    }

}