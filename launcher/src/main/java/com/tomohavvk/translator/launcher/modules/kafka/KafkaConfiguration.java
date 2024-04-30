package com.tomohavvk.translator.launcher.modules.kafka;

import com.tomohavvk.translator.kafka.EventsConsumer;
import com.tomohavvk.translator.kafka.EventsProducer;
import com.tomohavvk.translator.kafka.config.KafkaConfig;
import com.tomohavvk.translator.kafka.events.EndTranslateEvent;
import com.tomohavvk.translator.kafka.events.StartTranslateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableConfigurationProperties(KafkaConfig.class)
public class KafkaConfiguration {

    private final KafkaConfig config;

    public KafkaConfiguration(@Autowired KafkaConfig config) {
        this.config = config;
    }

    @Bean
    public EventsProducer<UUID, StartTranslateEvent> startTranslationEventsProducer() {
        return new EventsProducer<UUID, StartTranslateEvent>(config.producers().startTranslation());
    }

    @Bean
    public EventsProducer<UUID, EndTranslateEvent> endTranslationEventsProducer() {
        return new EventsProducer<UUID, EndTranslateEvent>(config.producers().endTranslation());
    }

    @Bean
    public EventsConsumer<UUID, StartTranslateEvent> startTranslationEventsConsumer() {
        return new EventsConsumer<UUID, StartTranslateEvent>(config.consumers().startTranslation());
    }

    @Bean
    public EventsConsumer<UUID, EndTranslateEvent> endTranslationEventsConsumer() {
        return new EventsConsumer<UUID, EndTranslateEvent>(config.consumers().endTranslation());
    }
}
