package com.fredymarleon.eventbuspattern.eventsBus

import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

class EventBus {
    private val _events = MutableSharedFlow<Any>()
    val events: SharedFlow<Any> = _events

    suspend fun publishEvent(event: Any) {
        _events.emit(event)
    }

    suspend inline fun <reified T> subscribeToEvents(crossinline onEventBugs: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEventBugs(event)
            }
    }

    //Singleton
    companion object {
        private val _evetBusInstance: EventBus by lazy { EventBus() }

        fun instance() = _evetBusInstance
    }
}