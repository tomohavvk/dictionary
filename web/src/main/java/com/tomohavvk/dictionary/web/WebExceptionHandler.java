package com.tomohavvk.dictionary.web;

import com.tomohavvk.dictionary.common.exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Order(-2)
@Component
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private record CodeWithError(int code, String message) {
    }

    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                               ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        var error = super.getError(request);
        log.error(request.path(), error);

        var errorMessage = switch (error) {
            case AppException e -> new CodeWithError(400, e.getMessage());
            case ResponseStatusException e -> new CodeWithError(e.getStatusCode().value(), e.getMessage());
            default -> new CodeWithError(500, "Internal Server Error");
        };

        return ServerResponse.status(errorMessage.code()).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of("error", errorMessage.message())));
    }
}