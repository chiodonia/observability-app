package ch.post.strm.observability.appproducer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Application {
    companion object {
        const val TOPIC_APP_FOO = "app.Foo"
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

