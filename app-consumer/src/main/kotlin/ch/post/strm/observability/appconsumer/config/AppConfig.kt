package ch.post.strm.observability.appconsumer.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

@Configuration
@EnableKafka
class AppConfig(val kafkaProperties: KafkaProperties, val meterRegistry: MeterRegistry) {

    @Bean
    fun consumer(): Consumer<String, String> {
        val consumer = KafkaConsumer(
            kafkaProperties.buildConsumerProperties(),
            StringDeserializer(),
            StringDeserializer()
        )
        KafkaClientMetrics(consumer).bindTo(meterRegistry)
        return consumer
    }

}