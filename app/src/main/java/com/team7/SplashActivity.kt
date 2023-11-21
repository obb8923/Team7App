package com.team7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var days:String?="null"
    private var workoutDays:String?="null"
    private var dietDays:String?="null"
    private var goalW:String="null"
    private var currW:String?="null"
    private var lastW:String?="null"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth
        db = Firebase.firestore
        Handler().postDelayed({
//            val intent = Intent(this, UseActivity::class.java)
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
    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        else{
            val ref = db.collection(user.uid).document("userInformation")
            ref.get()
                .addOnSuccessListener { document->
                    days = (document.getLong("days") ?: 0).toString() // Use 0 if "days" is null
                    workoutDays = (document.getLong("workoutDay") ?: 0).toString() // Use 0 if "days" is null
                    dietDays = (document.getLong("dietDay") ?: 0).toString() // Use 0 if "days" is null
                    goalW = (document.getLong("goalWeight") ?: 0).toString()
                    currW = (document.getLong("weight") ?: 0).toString()
                    lastW = (document.getLong("lastWeight") ?: 0).toString()
                    val intent = Intent(this,UseActivity::class.java)
                    intent.apply {
                        this.putExtra("user_uid",user.uid)
                        this.putExtra("user_displayName",user.displayName)
                        this.putExtra("user_phoneNumber",user.phoneNumber)
                        this.putExtra("ds",days)
                        this.putExtra("wds",workoutDays)
                        this.putExtra("dds",dietDays)
                        this.putExtra("gw",goalW)
                        this.putExtra("cw",currW)
                        this.putExtra("lw",lastW)
                    }
                    startActivity(intent)
                }
        }
    }
}