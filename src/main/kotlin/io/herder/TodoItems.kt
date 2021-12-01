package io.herder

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.function.Consumer


enum class STATE {
  DONE,
  TODO,
  DISCARDED
}

data class TodoItem(
  @Id var id: Long? = null,
  val state: STATE,
  val header: String,
  val body: String?
)

interface TodoItemRepository : ReactiveCrudRepository<TodoItem, Long> {}
@Configuration
class TodoConfig {

  @Bean
  fun eventSink(): Sinks.Many<TodoItem> {
    return Sinks.many().replay().latest()
  }

}
@Controller
class TodoItemController(
  val todoItemRepository: TodoItemRepository,
  val sink: Sinks.Many<TodoItem>
) {

  @MessageMapping("putItem")
  fun saveItem(todoItem: TodoItem): Mono<TodoItem> {
    return todoItemRepository.save(todoItem)
      .map { todoItem ->
        sink.tryEmitNext(todoItem);
        todoItem
      }
  }

  @MessageMapping("items")
  fun items() = todoItemRepository.findAll()

  @MessageMapping("updates")
  fun updates(): Flux<TodoItem> {
    return Flux.from(sink.asFlux())
  }
}

