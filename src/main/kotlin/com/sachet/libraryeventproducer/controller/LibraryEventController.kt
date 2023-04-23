package com.sachet.libraryeventproducer.controller

import com.sachet.libraryeventproducer.domain.LibraryEvent
import com.sachet.libraryeventproducer.domain.LibraryEventType
import com.sachet.libraryeventproducer.producer.LibraryEventProducer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/libraryevent")
class LibraryEventController(
    val libraryEventProducer: LibraryEventProducer
) {

    @PostMapping
    fun postLibraryEvent(@RequestBody libraryEvent: LibraryEvent): ResponseEntity<LibraryEvent> {
        libraryEvent.libraryId = UUID.randomUUID().toString()
        libraryEvent.eventType = LibraryEventType.NEW
//        libraryEventProducer.sendLibraryEvent(libraryEvent)
        libraryEventProducer.sendLibraryEventWithProducerRecord(libraryEvent = libraryEvent)
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent)
    }

    @PutMapping
    fun updateLibraryEvent(@RequestBody libraryEvent: LibraryEvent): ResponseEntity<Any> {
        if (libraryEvent.libraryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the library eventId")
        }
        libraryEvent.eventType = LibraryEventType.UPDATE
//        libraryEventProducer.sendLibraryEvent(libraryEvent)
        libraryEventProducer.sendLibraryEventWithProducerRecord(libraryEvent = libraryEvent)
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent)
    }

}