package com.aston.homework.config;

import com.aston.homework.dto.EventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, EventDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Aдреса серверов Kafka
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Класс для сериализации КЛЮЧА сообщения
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Класс для сериализации ЗНАЧЕНИЯ сообщения
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Настройка подтверждения получения сообщения:
        // "all" - ждать подтверждения от ВСЕХ реплик (наиболее надежно)
        // "1" - ждать подтверждения только от лидера
        // "0" - не ждать подтверждения (самый быстрый, но ненадежный)
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // Количество повторных попыток при временных ошибках сети
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        // Размер буфера для батчинга (группировки) сообщений в байтах
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        // Таймаут для батчинга - максимальное время ожидания перед отправкой батча
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, EventDto> kafkaTemplate(ProducerFactory<String, EventDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic("user_events_topic", 1, (short) 1);
    }
}
