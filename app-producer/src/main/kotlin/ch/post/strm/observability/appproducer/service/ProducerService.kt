package ch.post.strm.observability.appproducer.service

import ch.post.strm.observability.appproducer.Application.Companion.TOPIC_APP_FOO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.Future

@Service
class ProducerService(val producer: Producer<String, String>) {
    private val logger = LoggerFactory.getLogger(ProducerService::class.java)

    @Scheduled(fixedRate = 1000)
    fun produce() {
        send("0", "*".repeat(1024)) { m: RecordMetadata, e: Exception? ->
            when (e) {
                null -> logger.info("sent ${m.topic()}/${m.partition()}@${m.offset()}")
                else -> logger.error("General error", e)
            }
        }
    }

    fun produce(records: Int, size: Int, delay: Long): Flow<String> = channelFlow {
        repeat(records) { i ->
            send(i.toString(), "*".repeat(size)) { m: RecordMetadata, e: Exception? ->
                when (e) {
                    null -> launch { send("${m.topic()}/${m.partition()}@${m.offset()}: $i") }
                    else -> logger.error("General error", e)
                }
            }
            delay(delay)
        }
    }

    private fun send(key: String, value: String, callback: Callback): Future<RecordMetadata>? {
        return producer.send(
            ProducerRecord(
                TOPIC_APP_FOO,
                key,
                value
            ), callback
        )
    }

}

