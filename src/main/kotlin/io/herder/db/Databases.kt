package io.herder.db

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
@EnableR2dbcRepositories
class Databases {

  @Bean
  fun initializeDB(
    @Value("db/schema.sql") resource: Resource,
    connectionFactory: ConnectionFactory
  ): ConnectionFactoryInitializer {
    println("Initializing connection factory with the sweet sweet sql ")
    val initializer = ConnectionFactoryInitializer()
    initializer.setConnectionFactory(connectionFactory)
    initializer.setDatabasePopulator(
      ResourceDatabasePopulator(resource)
    )
    return initializer
  }

}
