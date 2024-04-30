package com.tomohavvk.translator.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaConfig(KafkaProducersConfig producers, KafkaConsumersConfig consumers) {

    public record KafkaProducersConfig(KafkaProducerConfig startTranslation, KafkaProducerConfig endTranslation) {

    }

    public record KafkaProducerRetryConfig(int attempts, int intervalMs) {
    }

    public record KafkaProducerConfig(String bootstrapServers, String schemaRegistryUrl, String topic, String ack,
            int requestTimeoutMs, KafkaProducerRetryConfig retry) {

    }

    public record KafkaConsumersConfig(KafkaConsumerConfig startTranslation, KafkaConsumerConfig endTranslation) {

    }

    public record KafkaConsumerConfig(String bootstrapServers, String schemaRegistryUrl, String topic, String groupId,
            String autoOffsetReset, int maxPollIntervalMs) {
    }
}
