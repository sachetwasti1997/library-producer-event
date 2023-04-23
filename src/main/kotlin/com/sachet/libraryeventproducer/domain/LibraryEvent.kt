package com.sachet.libraryeventproducer.domain

class LibraryEvent (
    var libraryId: String? = null,
    var eventType: LibraryEventType? = null,
    var book: Book? = null
)