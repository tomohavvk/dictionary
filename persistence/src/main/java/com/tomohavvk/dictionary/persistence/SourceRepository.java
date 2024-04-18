package com.tomohavvk.dictionary.persistence;

import com.tomohavvk.dictionary.persistence.entities.SourceEntity;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

public interface SourceRepository {
    void setConnectionFactory(ConnectionFactory connectionFactory);

    Flux<SourceEntity> selectSources(String sourceLanguage, int limit, int offset);

    Flux<Long> upsertSources(LinkedList<SourceEntity> sources);

    Mono<Long> deleteSource(SourceEntity source);

}