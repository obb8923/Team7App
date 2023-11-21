package com.team7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.team7.databinding.ActivityUseBinding

class UseActivity : AppCompatActivity() {
    private lateinit var profileFragment:ProfileFragment
    private lateinit var mainRecordFragment:MainRecordFragment
    private lateinit var moreFragment:MoreFragment
    private lateinit var binding: ActivityUseBinding
    private var userUid:String?= "nullUid"
    private var userDisplayName:String?= "nullDisplayName"
    private var userPhoneNumber:String?= "nullPhoneNumber"
    private var ds:String?="null"
    private var wds:String?="null"
    private var dds:String?="null"
    private var gw:String?="null"
    private var cw:String?="null"
    private var lw:String?="null"
    private var toast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userUid = intent.getStringExtra("user_uid")
        userDisplayName=intent.getStringExtra("user_displayName")
        userPhoneNumber=intent.getStringExtra("user_phoneNumber")
        ds = intent.getStringExtra("ds")
        wds = intent.getStringExtra("wds")
        dds = intent.getStringExtra("dds")
        gw = intent.getStringExtra("gw")
        cw = intent.getStringExtra("cw")
        lw = intent.getStringExtra("lw")

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
    override fun onStart() {
        super.onStart()
        fragmentManage()
    }
    override fun onResume() {
        super.onResume()
        supportFragmentManager.beginTransaction()
            .attach(profileFragment).commit()
    }
    override fun onPause() {
        super.onPause()
        toast?.cancel()
    }
    //Fragment 생성과 이동
    private fun fragmentManage(){
        profileFragment =ProfileFragment.newInstance(userDisplayName,userUid,ds,wds,dds,gw,cw,lw)
        mainRecordFragment =MainRecordFragment.newInstance(userUid)
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
                    if(currentFragment !is IconSelectionFragment){
                        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame,mainRecordFragment).commit()
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