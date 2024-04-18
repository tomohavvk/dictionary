package com.tomohavvk.dictionary.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> routs(Handlers handlers) {
        return route(POST("/api/v1/extract"), handlers::extract)
                .andRoute(POST("/api/v1/transform"), handlers::transform).andRoute(GET("/api/v1/load"), handlers::load);
    }
}
