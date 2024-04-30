package com.tomohavvk.translator.handlers;

import com.tomohavvk.translator.kafka.events.EndTranslateEvent;
import reactor.core.publisher.Mono;

public interface EndTranslateEventHandler {
    Mono<Long> handle(EndTranslateEvent event);
}
