package com.tomohavvk.translator.persistence;

import com.tomohavvk.translator.persistence.entities.TranslationEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TranslationsRepository {

    Mono<Long> upsertTranslation(TranslationEntity target);

    Flux<TranslationEntity> selectTranslations(String sourceLanguage, String targetLanguage, int limit, int offset);
}