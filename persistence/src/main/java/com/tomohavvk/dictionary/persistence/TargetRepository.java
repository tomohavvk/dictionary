package com.tomohavvk.dictionary.persistence;

import com.tomohavvk.dictionary.persistence.entities.TargetEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TargetRepository {

    Mono<Long> upsertTarget(TargetEntity target);

    Flux<TargetEntity> selectTargets(String sourceLanguage, String targetLanguage, int limit, int offset);
}