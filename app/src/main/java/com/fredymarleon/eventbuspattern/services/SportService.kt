package com.fredymarleon.eventbuspattern.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.fredymarleon.eventbuspattern.eventsBus.EventBus
import com.fredymarleon.eventbuspattern.eventsBus.SportEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SportService : Service() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun saveResult(result: SportEvent.ResultSuccess) {
        scope.launch {
            val response = if (result.isWarning) {
                SportEvent.ResultError(26, "Ocurrió un error al guardar")
            } else {
                SportEvent.SaveEvent
            }
            EventBus.instance().publishEvent(response)
        }
    }

    fun setupSubscribers(viewScope: CoroutineScope) {
        viewScope.launch {
            EventBus.instance().subscribeToEvents<SportEvent> { event ->
                when (event) {
                    is SportEvent.CloseAdEvent ->
                        Log.i("TAG", "Ad was closed. Send data to server...")
                    else -> {}
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    //Singleton
    companion object {
        private val _service: SportService by lazy { SportService() }

        fun instance() = _service
    }
}