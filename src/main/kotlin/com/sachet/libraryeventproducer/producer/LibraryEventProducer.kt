package com.sachet.libraryeventproducer.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.sachet.libraryeventproducer.domain.LibraryEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class LibraryEventProducer(
    var kafkaTemplate: KafkaTemplate<String, String>,
    var objectMapper: ObjectMapper
) {

    val LOGGER: Logger = LoggerFactory.getLogger(LibraryEventProducer::class.java)
    val topic = "library-events"

    fun sendLibraryEvent(libraryEvent: LibraryEvent) {
        val value = objectMapper.writeValueAsString(libraryEvent)
        libraryEvent.libraryId?.let { key ->
            val future = CompletableFuture<SendResult<String, String>>()
            try {
                kafkaTemplate
                    .sendDefault(key, value)
                    .whenComplete { res, ex ->
                        if (ex != null) {
                            future.completeExceptionally(ex)
                            handleFailure(key, value, ex)
                        } else {
                            future.complete(res)
                        }
                    }
                handleSuccess(key, value, future.get())
            }catch (ex: Exception) {
                future.completeExceptionally(ex)
            }

        }
    }

    fun sendLibraryEventWithProducerRecord(libraryEvent: LibraryEvent) {
        val value = objectMapper.writeValueAsString(libraryEvent)
        libraryEvent.libraryId?.let { key ->
            val future = CompletableFuture<SendResult<String, String>>()
            val record = createProducerRecord(key, value)
            try {
                kafkaTemplate
                    .send(record)
                    .whenComplete { res, ex ->
                        if (ex != null) {
                            future.completeExceptionally(ex)
                            handleFailure(key, value, ex)
                        } else {
                            future.complete(res)
                        }
                    }
                handleSuccess(key, value, future.get())
            }catch (ex: Exception) {
                future.completeExceptionally(ex)
            }

        }
    }

    private fun createProducerRecord(key: String, value: String): ProducerRecord<String, String> {
        val recordHeaders = listOf(RecordHeader("event-source", "scanner".toByteArray()))
        return ProducerRecord(
            topic,
            null,
            key,
            value,
            recordHeaders
        )
    }

    private fun handleFailure(key: String, value: String, ex: Throwable) {
        LOGGER.error("Error Sending the message and exception is {}", ex.message)
        try {
            throw Exception(ex)
        } catch (ex: Exception) {
            LOGGER.error("Error: {}", ex.message)
        }
    }

    private fun handleSuccess(key: String, value: String, result: SendResult<String, String>) {
        LOGGER.info("Message sent successfully for key: {}, value: {}, partition is: {}",
            key, value, result.recordMetadata.partition())
    }

}