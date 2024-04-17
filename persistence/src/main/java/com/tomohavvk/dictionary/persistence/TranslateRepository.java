package com.tomohavvk.dictionary.persistence;

import com.tomohavvk.dictionary.persistence.entities.SourceEntity;
import com.tomohavvk.dictionary.persistence.entities.TargetEntity;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

public interface TranslateRepository {
    void setConnectionFactory(ConnectionFactory connectionFactory);

    Flux<SourceEntity> streamSources(String sourceLanguage);

    Flux<Long> upsertSources(LinkedList<SourceEntity> sources);

    Mono<Long> deleteSource(SourceEntity source);

    Mono<Long> upsertTarget(TargetEntity target);
}