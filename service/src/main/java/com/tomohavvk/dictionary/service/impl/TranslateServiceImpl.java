package com.tomohavvk.dictionary.service.impl;

import com.google.cloud.translate.Translate.TranslateOption;
import com.tomohavvk.dictionary.common.models.ParseCommand;
import com.tomohavvk.dictionary.common.models.TranslateCommand;
import com.tomohavvk.dictionary.parser.WordsParser;
import com.tomohavvk.dictionary.persistence.TranslateRepository;
import com.tomohavvk.dictionary.persistence.entities.SourceEntity;
import com.tomohavvk.dictionary.persistence.entities.TargetEntity;
import com.tomohavvk.dictionary.service.TranslateService;
import com.tomohavvk.dictionary.service.TranslateUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TranslateServiceImpl implements TranslateService {
    private Logger logger = LoggerFactory.getLogger(TranslateServiceImpl.class);

    private final TransactionalOperator rxtx;

    private final WordsParser parser;
    private final TranslateUtils translateUtils;
    private final TranslateRepository translateRepository;

    @Override
    public Mono<Long> parse(ParseCommand command) {
        return parser.parse(command).onErrorResume(error -> {
            logger.error(error.getMessage());

            return Mono.just(0L);
        });
    }

    @Override
    public Mono<Long> translate(TranslateCommand command) {
        return translateRepository.streamSources(command.sourceLanguage())
                .flatMap(source -> proceed(source, command.targetLanguage())).collectList()
                .map(list -> (long) list.size()).onErrorResume(error -> {
                    logger.error(error.getMessage());

                    return Mono.just(0L);
                });
    }

    public Mono<Long> saveAndCleanup(SourceEntity source, TargetEntity target) {
        return translateRepository.upsertTarget(target).then(translateRepository.deleteSource(source))
                .as(rxtx::transactional);
    }

    private Flux<Long> proceed(SourceEntity source, String targetLanguage) {
        return getTranslation(source, targetLanguage).map(translation -> new TargetEntity(0L, source.source(),
                translation, source.sourceLanguage(), targetLanguage))
                .flatMap(target -> saveAndCleanup(source, target));
    }

    private Flux<String> getTranslation(SourceEntity source, String targetLanguage) {
        var options = TranslateOption.sourceLanguage(source.sourceLanguage()).targetLanguage(targetLanguage);

        return Flux.just(translateUtils.getTranslator().translate(source.source(), options).getTranslatedText());
    }

}