package com.tomohavvk.translator.persistence.impl;

import com.tomohavvk.translator.persistence.TranslationsRepository;
import com.tomohavvk.translator.persistence.entities.TranslationEntity;
import io.r2dbc.spi.ConnectionFactory;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TranslationsRepositoryImpl implements TranslationsRepository {

    private final DatabaseClient db;

    public TranslationsRepositoryImpl(@Autowired ConnectionFactory connectionFactory) {
        this.db = DatabaseClient.create(connectionFactory);
    }

    public Mono<Long> upsertTranslation(TranslationEntity target) {
        val query = """
                insert into translations (source, target, source_language, target_language)
                  values (:source, :target, :source_language, :target_language) ON CONFLICT (source, target) DO NOTHING""";

        return db.sql(query).bind("source", target.source()).bind("target", target.target())
                .bind("source_language", target.sourceLanguage()).bind("target_language", target.targetLanguage())
                .fetch().rowsUpdated();
    }

    public Flux<TranslationEntity> selectTranslations(String sourceLanguage, String targetLanguage, int limit,
            int offset) {
        val query = """
                select id, source, target, source_language, target_language from translations
                 where source_language = :sourceLanguage and target_language = :targetLanguage order by id limit :limit offset :offset""";

        return db.sql(query).bind("sourceLanguage", sourceLanguage).bind("targetLanguage", targetLanguage)
                .bind("limit", limit).bind("offset", offset).mapProperties(TranslationEntity.class).all();
    }
}
