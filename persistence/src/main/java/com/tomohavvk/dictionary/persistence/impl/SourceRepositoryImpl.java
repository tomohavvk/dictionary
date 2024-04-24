package com.tomohavvk.dictionary.persistence.impl;

import com.tomohavvk.dictionary.persistence.SourceRepository;
import com.tomohavvk.dictionary.persistence.entities.SourceEntity;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

@Repository
public class SourceRepositoryImpl implements SourceRepository {

    private DatabaseClient db;

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.db = DatabaseClient.create(connectionFactory);
    }

    public Flux<SourceEntity> selectSources(String sourceLanguage, int limit, int offset) {
        return db.sql(
                        "select id, source, source_language from sources where source_language = :sourceLanguage order by id limit :limit offset :offset")
                .bind("sourceLanguage", sourceLanguage).bind("limit", limit).bind("offset", offset)
                .mapProperties(SourceEntity.class).all();
    }

    public Flux<Long> upsertSources(LinkedList<SourceEntity> sources) {
        if (sources.isEmpty())
            return Flux.just(0L);
        else
            return db.inConnectionMany(connection -> {

                SourceEntity last = sources.removeLast();

                var statement = connection.createStatement(
                        "insert into sources (source, source_language) values ($1, $2) ON CONFLICT (source) DO NOTHING");

                for (var w : sources) {
                    statement.bind(0, w.source()).bind(1, w.sourceLanguage()).add();
                }

                statement.bind(0, last.source()).bind(1, last.sourceLanguage());

                return Flux.from(statement.execute()).flatMap(Result::getRowsUpdated);
            });
    }

    public Mono<Long> deleteSource(SourceEntity source) {
        return db.sql("delete from sources where id = :id").bind("id", source.id()).fetch().rowsUpdated();
    }
}
