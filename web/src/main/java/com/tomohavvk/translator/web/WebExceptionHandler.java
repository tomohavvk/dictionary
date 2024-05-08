package com.tomohavvk.translator.web;

import com.tomohavvk.translator.web.exceptions.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

    private record ErrorWithCode(String error, int code) {
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
        val error = super.getError(request);
        log.error(request.path(), error);

        val errorMessage = switch (error) {
        case InvalidRequestException e -> new ErrorWithCode(e.getMessage(), 400);
        case ResponseStatusException e -> new ErrorWithCode(e.getMessage(), e.getStatusCode().value());
        default -> new ErrorWithCode("Internal Server Error", 500);
        };

        return ServerResponse.status(errorMessage.code()).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of("error", errorMessage.error())));
    }
}