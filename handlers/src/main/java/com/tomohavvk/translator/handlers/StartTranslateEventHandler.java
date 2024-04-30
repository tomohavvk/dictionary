package com.tomohavvk.translator.handlers;

import com.tomohavvk.translator.kafka.events.StartTranslateEvent;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderResult;

import java.util.UUID;

public interface StartTranslateEventHandler {

    Flux<SenderResult<UUID>> handle(StartTranslateEvent event);
}
