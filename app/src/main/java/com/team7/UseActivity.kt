package com.team7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.team7.databinding.ActivityUseBinding

class UseActivity : AppCompatActivity() {
    private lateinit var profileFragment:ProfileFragment
    private lateinit var recordFragment:RecordFragment
    private lateinit var moreFragment:MoreFragment
    private lateinit var binding: ActivityUseBinding
    private var userUid:String?= "nullUid"
    private var userDisplayName:String?= "nullDisplayName"
    private var userPhoneNumber:String?= "nullPhoneNumber"
    private var toast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userUid = intent.getStringExtra("user_uid")
        userDisplayName=intent.getStringExtra("user_displayName")
        userPhoneNumber=intent.getStringExtra("user_phoneNumber")
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onStart() {
        super.onStart()
        fragmentManage()
    }

    override fun onPause() {
        super.onPause()
        toast?.cancel()
    }
    //Fragment 생성과 이동
    private fun fragmentManage(){
        profileFragment =ProfileFragment.newInstance(userDisplayName,userUid)
        recordFragment =RecordFragment.newInstance(userUid)
        moreFragment =MoreFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,profileFragment).commit()

        binding.bottomNavBar.setOnItemSelectedListener{
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentFrame)
            when(it.itemId){
                R.id.bottomNavProfile -> {
                    if(currentFragment !is ProfileFragment){
                        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, profileFragment).commit()
                    }}
                R.id.bottomNavRecord->{
                    if(currentFragment !is RecordFragment){
                        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,recordFragment).commit()
                    }}
                R.id.bottomNavMore->{
                    if(currentFragment !is MoreFragment){
                        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,moreFragment).commit()
                    }}
            }
            true
        }
    }
    //뒤로가기 버튼 클릭시
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveTaskToBack(true)
        }
    }

}