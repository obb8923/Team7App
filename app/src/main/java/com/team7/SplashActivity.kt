package com.team7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth

        Handler().postDelayed({
//            val intent = Intent(this, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivity(intent)
            finish()
        }, 30000)
    }

    override fun onStart() {
        super.onStart()
        // 사용자가 로그인되어 있는지 확인하고 UI를 업데이트
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(this,UseActivity::class.java)
            intent.apply {
                this.putExtra("user_uid",user.uid)
                this.putExtra("user_displayName",user.displayName)
                this.putExtra("user_phoneNumber",user.phoneNumber)
            }
            startActivity(intent)
        }
    }
}