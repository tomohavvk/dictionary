package com.tomohavvk.translator.handlers.impl;

import com.tomohavvk.translator.handlers.StartTranslateEventHandler;
import com.tomohavvk.translator.handlers.util.Translator;
import com.tomohavvk.translator.kafka.EventsConsumer;
import com.tomohavvk.translator.kafka.EventsProducer;
import com.tomohavvk.translator.kafka.events.EndTranslateEvent;
import com.tomohavvk.translator.kafka.events.EventMeta;
import com.tomohavvk.translator.kafka.events.StartTranslateEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.sender.SenderResult;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartTranslateEventHandlerImpl implements StartTranslateEventHandler {

    private final EventsProducer<UUID, EndTranslateEvent> endTranslationEventsProducer;
    private final EventsConsumer<UUID, StartTranslateEvent> startTranslationEventsConsumer;
    private final Translator translator;
    private Disposable disposable;

    public Flux<SenderResult<UUID>> handle(StartTranslateEvent event) {
        return translator.translate(event.getSource().toString(), event.getSourceLanguage().toString(),
                event.getTargetLanguage().toString()).flux().flatMap(translation -> {
                    val endTranslateEvent = new EndTranslateEvent(event.getSource(), translation,
                            event.getSourceLanguage(), event.getTargetLanguage(),
                            new EventMeta(UUID.randomUUID(), LocalDateTime.now().toString()));

                    return endTranslationEventsProducer.produce(endTranslateEvent.getMeta().getId(), endTranslateEvent);
                });
    }

    @PostConstruct
    private void handleEvents() {
        disposable = startTranslationEventsConsumer.consume().doOnError(e -> log.error("error: {}", e.getMessage()))
                .flatMap(record -> {
                    ReceiverOffset offset = record.receiverOffset();
                    StartTranslateEvent event = record.value();

                    log.info("received message: topic-partition {} offset {}  key {} value {}", offset.topicPartition(),
                            offset.offset(), record.key().toString(), record.value());

                    return handle(event).doOnNext(__ -> offset.acknowledge());
                }).subscribe();
    }

    @PreDestroy
    private void dispose() {
        if (Objects.nonNull(disposable)) {
            log.info("disposing the consumer");
            disposable.dispose();
        }
    }
}