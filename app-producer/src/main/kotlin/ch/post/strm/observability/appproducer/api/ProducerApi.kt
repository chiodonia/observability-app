package ch.post.strm.observability.appproducer.api

import ch.post.strm.observability.appproducer.service.ProducerService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ProducerApi(val producer: ProducerService) {

    @GetMapping("/produce", params = ["records", "size", "delay"])
    fun produce(
        @RequestParam("records") records: Int,
        @RequestParam("size") size: Int,
        @RequestParam("delay") delay: Long
    ): Flow<String> {
        return producer.produce(records, size, delay).map { str -> "$str<br>" }
    }

}
