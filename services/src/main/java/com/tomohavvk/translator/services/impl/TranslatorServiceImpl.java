package com.tomohavvk.translator.services.impl;

import com.tomohavvk.translator.common.commands.LoadTranslationsCommand;
import com.tomohavvk.translator.common.commands.TranslateCommand;
import com.tomohavvk.translator.common.models.TranslationSource;
import com.tomohavvk.translator.extractor.WordsExtractor;
import com.tomohavvk.translator.kafka.EventsProducer;
import com.tomohavvk.translator.kafka.events.EventMeta;
import com.tomohavvk.translator.kafka.events.StartTranslateEvent;
import com.tomohavvk.translator.persistence.TranslationsRepository;
import com.tomohavvk.translator.persistence.entities.TranslationEntity;
import com.tomohavvk.translator.services.TranslatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatorServiceImpl implements TranslatorService {

    private final WordsExtractor extractor;
    private final EventsProducer<UUID, StartTranslateEvent> startTranslationEventsProducer;

    private final TranslationsRepository targetRepository;

    @Override
    public Mono<Long> translate(TranslateCommand command) {
        return extractor.extract(command).map(source -> new TranslationSource(source, command.sourceLanguage()))
                .flatMap(source -> {
                    var startTranslateEvent = new StartTranslateEvent(source.source(), command.sourceLanguage(),
                            command.targetLanguage(), new EventMeta(UUID.randomUUID(), LocalDateTime.now().toString()));
                    return startTranslationEventsProducer
                            .produce(startTranslateEvent.getMeta().getId(), startTranslateEvent).map(__ -> 1L);
                }).reduce(Long::sum);
    }

    @Override
    public Flux<TranslationEntity> loadTranslations(LoadTranslationsCommand command) {
        return targetRepository.selectTranslations(command.sourceLanguage(), command.targetLanguage(), command.limit(),
                command.offset());
    }
}