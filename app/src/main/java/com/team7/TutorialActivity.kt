package com.team7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.team7.databinding.ActivityTutorialBinding



class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        inquiryButton()
        tutorialButton()
    }
    private fun inquiryButton(){

    }
    private fun tutorialButton(){
        
    }
}