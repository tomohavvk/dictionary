package com.tomohavvk.translator.kafka;

import com.tomohavvk.translator.kafka.config.KafkaConfig.KafkaConsumerConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class EventsConsumer<K, E> {

    private final KafkaReceiver<K, E> consumer;

    public EventsConsumer(KafkaConsumerConfig config) {
        this.consumer = configureConsumer(config);
    }

    public Flux<ReceiverRecord<K, E>> consume() {
        return consumer.receive();
    }

    private KafkaReceiver<K, E> configureConsumer(KafkaConsumerConfig config) {

        Map<String, Object> props = Map.of("schema.registry.url", config.schemaRegistryUrl(),
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers(), ConsumerConfig.GROUP_ID_CONFIG,
                config.groupId(), ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.autoOffsetReset(),
                ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, config.maxPollIntervalMs(),
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false, ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                UUIDDeserializer.class, ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class,
                KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        ReceiverOptions<K, E> receiverOptions = ReceiverOptions.<K, E> create(props)
                .subscription(Collections.singleton(config.topic())).commitBatchSize(1000).maxCommitAttempts(3)
                .commitInterval(Duration.of(300, ChronoUnit.MILLIS))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));

        return KafkaReceiver.create(receiverOptions);
    }
}
