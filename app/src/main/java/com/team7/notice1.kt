package com.team7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.team7.databinding.ActivityNotice1Binding

class notice1 : AppCompatActivity() {
    private lateinit var binding:ActivityNotice1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotice1Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        backButton()
    }
    private fun backButton() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

}