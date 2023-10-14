package com.team7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.team7.databinding.ActivityMainBinding
import com.team7.databinding.ActivityUseBinding

class UseActivity : AppCompatActivity() {
    private lateinit var profileFragment:ProfileFragment
    private lateinit var recordFragment:RecordFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var moreFragment:MoreFragment
    private lateinit var binding: ActivityUseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFragment()
        replaceFragment()
    }
    private fun initFragment(){
        profileFragment =ProfileFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragmentFrame,profileFragment).commit()
    }
    private fun replaceFragment(){
        binding.bottomNavBar.setOnItemSelectedListener{
            when(it.itemId){
                R.id.bottomNavProfile -> {
                    profileFragment =ProfileFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,profileFragment).commit()
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