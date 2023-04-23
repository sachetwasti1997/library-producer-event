package com.sachet.libraryeventproducer.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class AutoCreateConfig {

    @Bean
    fun newTopic(): NewTopic = TopicBuilder.name("library-events")
        .partitions(3)
        .replicas(3)
        .build()

}