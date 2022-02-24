package com.example.appsocket.ui.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appsocket.databinding.ActivityContainerBinding

class Container : AppCompatActivity() {

    private lateinit var binding: ActivityContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //

    }
}