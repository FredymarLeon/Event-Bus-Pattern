package com.fredymarleon.eventbuspattern

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fredymarleon.eventbuspattern.databinding.ActivityMainBinding
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

    private fun setupSubscribers() {
        lifecycleScope.launch {
            EventBus.instance().subscribeToEvents<SportEvent> { event ->
                binding.srlResults.isRefreshing = false
                when (event) {
                    is SportEvent.ResultSuccess ->
                        adapter.add(event)

                    else -> {}
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
    override fun onClick(result: SportEvent.ResultSuccess) {}
}