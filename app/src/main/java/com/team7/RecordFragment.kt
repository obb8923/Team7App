package com.team7

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team7.databinding.FragmentRecordBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordFragment:Fragment() {
    companion object{
        fun newInstance(userUid:String?):RecordFragment{
            val fragment = RecordFragment()
            val args = Bundle()
            args.putString("userUid", userUid)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var binding : FragmentRecordBinding
    private lateinit var db:FirebaseFirestore
    private lateinit var uid : String
    val currentDate = LocalDate.now()//시간
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd") //날짜 형식 포맷 지정
    val formattedDate = currentDate.format(formatter)
    private var foodSet = hashMapOf(
        "kcal" to arrayListOf<Float>(0.0f,0.0f,0.0f,0.0f),
        "carbohydrates" to arrayListOf<Float>(0.0f,0.0f,0.0f,0.0f),
        "protein" to arrayListOf<Float>(0.0f,0.0f,0.0f,0.0f),
        "fat" to arrayListOf<Float>(0.0f,0.0f,0.0f,0.0f)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        uid =arguments?.getString("userUid")!!
    }

    override fun onResume() {
        super.onResume()
        testRecord()
        testR()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root }
    private fun testR(){
        val dd = binding.dd
        dd.text = uid
        db.collection(uid).document("foodSet"+formattedDate)
            .update(
                mapOf(
                    "name" to "불고기",
                    "kcal" to 100,
                ),
            )
    }
    private fun testRecord(){
        val recordB = binding.recordB
        recordB.setOnClickListener {
            db.collection(uid)
                .document("foodSet"+formattedDate+3)
                .set(foodSet)
            //삭제

            db.collection(uid).document("P4iAtL0oRKHi1zLWAYxG")
                .delete()
            //찾기

            val docRef = db.collection(uid).document("foodSet"+formattedDate)
            docRef.get()

        }
    }
    private fun testBb(){
    val recordBb = binding.recordBb
    recordBb.setOnClickListener {
        db.collection(uid)
            .document("foodSet"+formattedDate)
            .set(foodSet)
    }
    }
}