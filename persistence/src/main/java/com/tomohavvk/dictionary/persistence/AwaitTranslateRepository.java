package com.tomohavvk.dictionary.persistence;

import com.tomohavvk.dictionary.persistence.entities.AwaitTranslateEntity;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;

import java.util.LinkedList;

public interface AwaitTranslateRepository {
    void setConnectionFactory(ConnectionFactory connectionFactory);

    Flux<Long> upsert(LinkedList<AwaitTranslateEntity> words);
}