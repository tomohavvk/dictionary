package com.tomohavvk.translator.kafka;

import com.tomohavvk.translator.kafka.config.KafkaConfig.KafkaProducerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.UUIDSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.Map;
import lombok.val;

@Slf4j
public class EventsProducer<K, E> {

    private final KafkaProducerConfig config;
    private final KafkaSender<K, E> sender;

    public EventsProducer(KafkaProducerConfig config) {
        this.config = config;
        this.sender = configureProducer(config);
    }

    public Flux<SenderResult<K>> produce(K key, E event) {
        val record = SenderRecord.create(new ProducerRecord<>(config.topic(), key, event), key);

        return sender.send(Mono.just(record)).doOnError(e -> log.error("failed to send message", e))
                .doOnNext(result -> {
                    RecordMetadata metadata = result.recordMetadata();
                    log.info("message {} produced successfully, topic-partition={}-{} offset={}",
                            result.correlationMetadata(), metadata.topic(), metadata.partition(), metadata.offset());
                });
    }

    @PreDestroy
    private void close() {
        log.info("closing the producer for topic {}", config.topic());

        sender.close();
    }

    private KafkaSender<K, E> configureProducer(KafkaProducerConfig config) {

        Map<String, Object> props = Map.of("schema.registry.url", config.schemaRegistryUrl(),
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers(), ProducerConfig.ACKS_CONFIG,
                config.ack(), ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, config.requestTimeoutMs(),
                ProducerConfig.RETRIES_CONFIG, config.retry().attempts(), ProducerConfig.RETRY_BACKOFF_MS_CONFIG,
                config.retry().intervalMs(), ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        return KafkaSender.create(SenderOptions.create(props));
    }
}
