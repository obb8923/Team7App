package com.team7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.team7.databinding.ActivityMainBinding
import com.team7.databinding.ActivityUseBinding

class UseActivity : AppCompatActivity() {
    private lateinit var profileFragment:ProfileFragment
    private lateinit var recordFragment:RecordFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var moreFragment:MoreFragment
    private lateinit var binding: ActivityUseBinding
    private var userUid:String?= "nullUid"
    private var userDisplayName:String?= "nullDisplayName"
    private var userPhoneNumber:String?= "nullPhoneNumber"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userUid = intent.getStringExtra("user_uid")
        userDisplayName=intent.getStringExtra("user_displayName")
        userPhoneNumber=intent.getStringExtra("user_phoneNumber")
        initFragment()
        replaceFragment()
    }
    //첫화면
    private fun initFragment(){
        profileFragment =ProfileFragment.newInstance(userDisplayName)
        supportFragmentManager.beginTransaction().add(R.id.fragmentFrame,profileFragment).commit()
    }
    //fragment 바꾸기
    private fun replaceFragment(){
        binding.bottomNavBar.setOnItemSelectedListener{
            when(it.itemId){
                R.id.bottomNavProfile -> {
                    val userDisplayName = intent.getStringExtra("user_displayName")
                    profileFragment = ProfileFragment.newInstance(userDisplayName)
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, profileFragment).commit()
                }
                R.id.bottomNavRecord->{
                    recordFragment =RecordFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,recordFragment).commit()
                }
                R.id.bottomNavCommunity->{
                    communityFragment =CommunityFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,communityFragment).commit()
                }
                R.id.bottomNavMore->{
                    moreFragment =MoreFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,moreFragment).commit()
                }
            }
            true
        }
    }



}