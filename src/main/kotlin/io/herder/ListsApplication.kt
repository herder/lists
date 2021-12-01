package io.herder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ListsApplication {

  @Bean
  fun testing(): String  {
    println("HEEEEEJ")
    return "HEEEEJ"
  }
}

fun main(args: Array<String>) {
  runApplication<ListsApplication>(*args)
}
