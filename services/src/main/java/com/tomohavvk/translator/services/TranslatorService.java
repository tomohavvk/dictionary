package com.tomohavvk.translator.services;

import com.tomohavvk.translator.common.commands.LoadTranslationsCommand;
import com.tomohavvk.translator.common.commands.TranslateCommand;
import com.tomohavvk.translator.persistence.entities.TranslationEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TranslatorService {
    Mono<Long> translate(TranslateCommand command);

    Flux<TranslationEntity> loadTranslations(LoadTranslationsCommand command);
}
