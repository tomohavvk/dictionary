package com.tomohavvk.dictionary.persistence;

import com.tomohavvk.dictionary.persistence.entities.AwaitTranslateEntity;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.LinkedList;

@Repository
public class AwaitTranslateRepositoryImpl implements AwaitTranslateRepository {

    private DatabaseClient client;

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.client = DatabaseClient.create(connectionFactory);
    }

    @Transactional
    public Flux<Long> upsert(LinkedList<AwaitTranslateEntity> words) {

        if (words.isEmpty())
            return Flux.just(0L);
        else
            return this.client.inConnectionMany(connection -> {

                AwaitTranslateEntity last = words.removeLast();

                var statement = connection.createStatement(
                        "INSERT INTO await_translate (word) values ($1) ON CONFLICT (word) DO NOTHING");

                for (var w : words) {
                    statement.bind(0, w.word()).add();
                }

                statement.bind(0, last.word());

                return Flux.from(statement.execute()).flatMap(Result::getRowsUpdated);
            });

    }
}
