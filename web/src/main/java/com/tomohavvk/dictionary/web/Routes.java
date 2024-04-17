package com.tomohavvk.dictionary.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> routs(Handlers handlers) {
        return route(POST("/api/v1/parse"), handlers::parse).andRoute(POST("/api/v1/translate"), handlers::translate);
    }
}
