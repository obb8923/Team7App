package com.team7

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team7.databinding.ActivityProfileSettingBinding

class ProfileSettingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileSettingBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var uid : String
    var userDisplayName :String? = "사용자 이름"

    //firebase storage
    var storage = Firebase.storage
    var storageRef = storage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        uid = intent.getStringExtra("user_uid")!!
        userDisplayName = intent.getStringExtra("user_name")

    }

    override fun onResume() {
        super.onResume()
        userInformUpdate()
        userName()
        userInformCorrectionButton(this)
    }
    private fun userName() {
        val userDisplayNameTextView = binding.userName
        userDisplayNameTextView.text = userDisplayName
    }
    private fun userInformUpdate(){
        val userWeightText = binding.userInformWeight
        val userHeightText = binding.userInformHeight
        db.collection(uid).document("userInformation")
            .get().addOnSuccessListener{document->
                userWeightText.text=getString(R.string.userInformWeight,document.getDouble("weight")!!.toFloat())
                userHeightText.text=getString(R.string.userInformHeight,document.getDouble("height")!!.toFloat())
            }
    }
    private fun userInformCorrectionButton(context: Context){
        val b = binding.userInformCorrectionButton
        b.setOnClickListener {
            // 다이얼로그 빌더 생성
            val builder = AlertDialog.Builder(context)

            // XML 레이아웃을 이용하여 다이얼로그 뷰 설정
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.height_weight_dialog, null)
            builder.setView(dialogView)
            // 다이얼로그 버튼 클릭 이벤트 설정
            builder.setPositiveButton("확인") { dialog, which ->
                // 확인 버튼 클릭 시 수행할 동작
                // 키,몸무게 수정
                val editText1 = dialogView.findViewById<EditText>(R.id.dialog)
                val editText2 = dialogView.findViewById<EditText>(R.id.dialog_et)
                val weight =editText2.text.toString().toFloatOrNull() ?: 0.0f
                val height = editText1.text.toString().toFloatOrNull() ?: 0.0f
                //유저 정보 변경
                db.collection(uid).document("userInformation")
                    .update("weight",weight)
                db.collection(uid).document("userInformation")
                    .update("height",height)
                userInformUpdate()
            }
            // 다이얼로그 생성 및 표시
            val dialog = builder.create()
            dialog.show()
        }
        }
    }