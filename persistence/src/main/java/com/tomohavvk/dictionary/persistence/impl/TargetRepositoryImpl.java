package com.tomohavvk.dictionary.persistence.impl;

import com.tomohavvk.dictionary.persistence.TargetRepository;
import com.tomohavvk.dictionary.persistence.entities.TargetEntity;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TargetRepositoryImpl implements TargetRepository {

    private DatabaseClient db;

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.db = DatabaseClient.create(connectionFactory);
    }

    public Mono<Long> upsertTarget(TargetEntity target) {
        var query = """
                insert into targets (source, target, source_language, target_language)
                  values (:source, :target, :source_language, :target_language) ON CONFLICT (source, target) DO NOTHING""";

        return db.sql(query).bind("source", target.source()).bind("target", target.target())
                .bind("source_language", target.sourceLanguage()).bind("target_language", target.targetLanguage())
                .fetch().rowsUpdated();
    }

    public Flux<TargetEntity> selectTargets(String sourceLanguage, String targetLanguage, int limit, int offset) {
        var query = """
                select id, source, target, source_language, target_language from targets
                 where source_language = :sourceLanguage and target_language = :targetLanguage order by id limit :limit offset :offset""";

        return db.sql(query).bind("sourceLanguage", sourceLanguage).bind("targetLanguage", targetLanguage)
                .bind("limit", limit).bind("offset", offset).mapProperties(TargetEntity.class).all();
    }
}
