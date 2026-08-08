package com.fredymarleon.eventbuspattern

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fredymarleon.eventbuspattern.adapters.OnClickListener
import com.fredymarleon.eventbuspattern.adapters.ResultAdapter
import com.fredymarleon.eventbuspattern.dataAcces.getAdEventsInRealtime
import com.fredymarleon.eventbuspattern.dataAcces.getResultEventsInRealtime
import com.fredymarleon.eventbuspattern.dataAcces.someTime
import com.fredymarleon.eventbuspattern.databinding.ActivityMainBinding
import com.fredymarleon.eventbuspattern.eventsBus.EventBus
import com.fredymarleon.eventbuspattern.eventsBus.SportEvent
import com.fredymarleon.eventbuspattern.services.SportService
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()
        setupSubscribers()
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(this)

    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.srlResults.setOnRefreshListener {
            adapter.clear()
            getEvents()
            binding.btnAd.isVisible = true
        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    val events = getAdEventsInRealtime()
                    EventBus.instance().publishEvent(events.first())
                }
            }
            setOnLongClickListener { view ->
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    EventBus.instance().publishEvent(SportEvent.CloseAdEvent)
                    view.isVisible = false
                }
                true
            }
        }
    }

    private fun setupSubscribers() {
        lifecycleScope.launch {
            SportService.instance().setupSubscribers(this)
            EventBus.instance().subscribeToEvents<SportEvent> { event ->
                binding.srlResults.isRefreshing = false
                when (event) {
                    is SportEvent.ResultSuccess ->
                        adapter.add(event)

                    is SportEvent.ResultError -> Snackbar.make(
                        binding.root, "Code: ${event.errorCode}, Message: ${event.errorMessage}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    is SportEvent.SaveEvent -> Toast.makeText(
                        this@MainActivity,
                        "Guardado",
                        Toast.LENGTH_SHORT
                    ).show()

                    is SportEvent.AdEvent -> Toast.makeText(
                        this@MainActivity,
                        "Ad click. Send data to server...",
                        Toast.LENGTH_SHORT
                    ).show()

                    is SportEvent.CloseAdEvent -> binding.btnAd.isVisible = false
                }
            }
        }
    }

    private fun getEvents() {
        lifecycleScope.launch {
            val events = getResultEventsInRealtime()
            events.forEach { event ->
                delay(someTime().milliseconds)
                EventBus.instance().publishEvent(event)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.srlResults.isRefreshing = true
        getEvents()
    }

    /*
    * OnClickListener
    */
    override fun onClick(result: SportEvent.ResultSuccess) {
        binding.srlResults.isRefreshing = true
        lifecycleScope.launch {
           // EventBus.instance().publishEvent(SportEvent.SaveEvent)
            SportService.instance().saveResult(result)
        }
    }
}