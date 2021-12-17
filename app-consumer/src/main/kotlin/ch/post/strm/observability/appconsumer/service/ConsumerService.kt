package ch.post.strm.observability.appconsumer.service

import ch.post.strm.observability.appconsumer.Application.Companion.TOPIC_APP_FOO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration.ofMillis
import javax.annotation.PostConstruct

@Service
class ConsumerService(val consumer: Consumer<String, String>) {
    private val logger = LoggerFactory.getLogger(ConsumerService::class.java)

    @PostConstruct
    fun init() = consumer.subscribe(listOf(TOPIC_APP_FOO), object : ConsumerRebalanceListener {
        override fun onPartitionsRevoked(partitions: Collection<TopicPartition>) {
            partitions.forEach { logger.info("Partition revoked: {}/{}", it.topic(), it.partition()) }
        }

        override fun onPartitionsAssigned(partitions: Collection<TopicPartition>) {
            partitions.forEach { logger.info("Partition assigned: {}/{}", it.topic(), it.partition()) }
        }
    })

    fun consume(polls: Int, duration: Long, delay: Long): Flow<String> = channelFlow {
        repeat(polls) {
            consumer
                .poll(ofMillis(duration)).forEach {
                    val foo = "${it.topic()}/${it.partition()}@${it.offset()}: ${it.key()}"
                    logger.info(foo)
                    send(foo)
                    delay(delay)
                }
            consumer.commitSync()
        }
    }

}

