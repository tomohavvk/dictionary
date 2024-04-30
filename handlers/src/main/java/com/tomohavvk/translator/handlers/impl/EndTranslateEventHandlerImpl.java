package com.tomohavvk.translator.handlers.impl;

import com.tomohavvk.translator.handlers.EndTranslateEventHandler;
import com.tomohavvk.translator.handlers.util.Translator;
import com.tomohavvk.translator.kafka.EventsConsumer;
import com.tomohavvk.translator.kafka.events.EndTranslateEvent;
import com.tomohavvk.translator.persistence.TranslationsRepository;
import com.tomohavvk.translator.persistence.entities.TranslationEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndTranslateEventHandlerImpl implements EndTranslateEventHandler {

    private final TransactionalOperator rxtx;
    private final EventsConsumer<UUID, EndTranslateEvent> endTranslationEventsConsumer;
    private final Translator translator;
    private final TranslationsRepository targetRepository;
    private Disposable disposable;

    public Mono<Long> handle(EndTranslateEvent event) {
        var entity = new TranslationEntity(0L, event.getSource().toString(), event.getTarget().toString(),
                event.getSourceLanguage().toString(), event.getTargetLanguage().toString());

        return targetRepository.upsertTranslation(entity).as(rxtx::transactional);
    }

    @PostConstruct
    private void handleEvents() {
        disposable = endTranslationEventsConsumer.consume().doOnError(e -> log.error("error: {}", e.getMessage()))
                .flatMap(record -> {
                    ReceiverOffset offset = record.receiverOffset();
                    EndTranslateEvent event = record.value();

                    log.debug("received message: topic-partition {} offset {}  key {} value {}",
                            offset.topicPartition(), offset.offset(), record.key().toString(), record.value());

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