package ch.post.strm.observability.appproducer.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

@Configuration
@EnableKafka
class AppConfig(val kafkaProperties: KafkaProperties, val meterRegistry: MeterRegistry) {

    @Bean
    fun producer(): Producer<String, String> {
        val producer = KafkaProducer(
            kafkaProperties.buildProducerProperties(),
            StringSerializer(),
            StringSerializer()
        )
        KafkaClientMetrics(producer).bindTo(meterRegistry)
        return producer
    }

}