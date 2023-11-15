package com.team7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.team7.databinding.ActivityMainBinding
import com.team7.databinding.ActivityNoticeBinding

class NoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        backButton()
        notice1()
    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }
    private fun notice1(){
        binding.moreNoticeButton1.setOnClickListener {
            val intent = Intent(this,notice1::class.java)
            startActivity(intent)
        }
    }

}