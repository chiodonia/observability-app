package ch.post.strm.observability.appconsumer.api

import ch.post.strm.observability.appconsumer.service.ConsumerService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ConsumerApi(val consumer: ConsumerService) {

    @GetMapping("/consume", params = ["polls", "duration", "delay"])
    fun consume(
        @RequestParam("polls") polls: Int,
        @RequestParam("duration") duration: Long,
        @RequestParam("delay") delay: Long
    ): Flow<String> {
        return consumer.consume(polls, duration, delay).map { str -> "$str<br>" }
    }
}
