package com.team7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object
    {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
        // private const val webClientId ="945186122771-1lkmcbftr2lell1o1kc0hm86t8oqda3f.apps.googleusercontent.com"
        private const val webClientId="945186122771-mht9a4ktrudvuhckfbi9etordjpir4on.apps.googleusercontent.com"

    }
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInButton: SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //구글 로그인 옵션 구성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)//웹클라이언트 ID 요청
            .requestEmail()//이메일 정보 요청
            .build()

        //googleSignInClient 초기화
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase Auth 초기화
        auth = Firebase.auth
        //로그인 버튼
        googleSignInButton=binding.LoginButton
        googleSignInButton.setOnClickListener{
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        //구글 Sign-In 결과  처리
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // 구글 로그인 성공, authenticate with Firebase
                //account = 모든 구글 로그인 정보
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!,account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String,account:GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 시 UI
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // 로그인 실패 시 UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun signIn() {
        //여기서 인증을 받고 넘어올 것임
        val signInIntent = googleSignInClient.signInIntent
        //인증을 받은 결과를 받는다
        startActivityForResult(signInIntent, RC_SIGN_IN)
        //RC_SIGN_IN을 가지고 onActivityResult 실행
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            Toast.makeText(this,"Google 로그인 실패",Toast.LENGTH_SHORT).show()
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