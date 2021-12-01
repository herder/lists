package io.herder

import io.rsocket.core.Resume
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.rsocket.server.RSocketServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*
@Controller
class Controllers {

  @MessageMapping("items")
  fun item(item: TodoItem) {

  }

  @MessageMapping("hello")
  fun hello(name: TodoItem): Flux<String> {
    Hooks.onErrorDropped { }

    return Flux.generate<String?> { sink -> sink.next("hello, $name ${Date()}") }
      .delayElements(Duration.ofSeconds(1))
      .onErrorContinue { t, u -> println("Error: $t, $u") }
      .doOnCancel { println("Cancelled") }
  }

  @Bean
  fun runner(): ApplicationRunner {
    return ApplicationRunner { args ->
      {
        System.err.println(args)
      }
    }
  }

  @Bean
  fun serverProcessor(): RSocketServerCustomizer {
    val resume = Resume()
      .retry(Retry.backoff(
        10,
        Duration.ofSeconds(10)
      ).doBeforeRetry { s -> System.err.println("Disconnected. Trying to resume... $s") })
      .sessionDuration(Duration.ofMinutes(5))

    return RSocketServerCustomizer { rsocketServer -> rsocketServer.resume(resume) }

  }
}
