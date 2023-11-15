package com.team7

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team7.databinding.FragmentProfileBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ProfileFragment:Fragment() {
    companion object{
        fun newInstance(userDisplayName: String?,userUid:String?): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString("userDisplayName", userDisplayName)
            args.putString("userUid", userUid)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var binding : FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var uid : String
    private lateinit var userDisplayName :String


    /*private val currentDate = LocalDate.now()//시간
    private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd") //날짜 형식 포맷 지정
    private val formattedDate = currentDate.format(formatter)*/

    private var userInform = hashMapOf(
        "name" to "null",
        "height" to 0.0f,
        "weight" to 0.0f,
        "goalWeight" to 0.0f,
        "lastWeight" to 0.0f,
        "day" to 0,
        "days" to 1,
        "workoutDay" to 0,
        "dietDay" to 0
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        userDisplayName=arguments?.getString("userDisplayName")!!
        uid =arguments?.getString("userUid")!!
        //calorie progress bar
        kcalProgress()

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        daysUpdate()
        //read weight data at firestore
        weightUpdate()

    }
    override fun onResume() {
        super.onResume()
        val context = requireContext()
        profileButton()
        isFirstTimeOpen(context)
        weightCorrectionButton()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = FragmentProfileBinding.bind(requireView()) // binding 해제
    }

    private fun profileButton(){
        val profileButton = binding.profileProfileButton
        profileButton.text =userDisplayName
        profileButton.setOnClickListener {
                val intent = Intent(getActivity(),ProfileSettingActivity::class.java)
                intent.apply {
                    this.putExtra("user_name",userDisplayName)
                    this.putExtra("user_uid",uid)
            }
                startActivity(intent)
        }
    }

    private fun daysUpdate(){
        //전체 운동 식단
        val allDay = binding.allDay
        val workoutDay = binding.workoutDay
        val dietDay = binding.dietDay
        //update
        val ref = db.collection(uid).document("userInformation")
        ref.get()
            .addOnSuccessListener { document->
                val days = document.getLong("days") ?: 0 // Use 0 if "days" is null
                val daysString = days.toString()
                allDay.text = getString(R.string.nullDays,daysString)
                val workoutDays = document.getLong("workoutDay") ?: 0 // Use 0 if "days" is null
                val workoutDaysString = workoutDays.toString()
                workoutDay.text = getString(R.string.nullDays,workoutDaysString)
                val dietDays = document.getLong("dietDay") ?: 0 // Use 0 if "days" is null
                val dietDaysString = dietDays.toString()
                dietDay.text = getString(R.string.nullDays,dietDaysString)
            }

    }



    private fun weightCorrectionButton(){
        var w:Float=0.0f
        db.collection(uid).document("userInformation")
            .get().addOnSuccessListener { document->
                w = document.getDouble("weight")!!.toFloat()
            }
        val weightButton = binding.weightCorrectionButton
        weightButton.setOnClickListener {
            // 다이얼로그 빌더 생성
            val builder = AlertDialog.Builder(context)

            // XML 레이아웃을 이용하여 다이얼로그 뷰 설정
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.weight_dialog, null)
            builder.setView(dialogView)
            // 다이얼로그 버튼 클릭 이벤트 설정
            builder.setPositiveButton("확인") { dialog, which ->
                // 확인 버튼 클릭 시 수행할 동작
                // 목표, 현재 몸무게 수정 및 표시
                val editText1 = dialogView.findViewById<EditText>(R.id.dialog)
                val editText2 = dialogView.findViewById<EditText>(R.id.dialog_et)

                //유저 정보 변경 - 몸무게
                val ref = db.collection(uid).document("userInformation")
                ref.update("lastWeight",w)
                ref.update("weight",editText2.text.toString().toFloatOrNull() ?: 0.0f)
                ref.update("goalWeight",editText1.text.toString().toFloatOrNull() ?: 0.0f)
                weightUpdate()

            }
            // 다이얼로그 생성 및 표시
            val dialog = builder.create()
            dialog.show()
        }
    }
    private fun weightUpdate(){
        val goalWeightTextView = binding.goalWeight
        val currentWeightTextView = binding.currentWeight
        val ref =db.collection(uid).document("userInformation")
        ref.get().addOnSuccessListener { document->
            val currentWeight= document.getDouble("weight")!!.toFloat()
            val goalWeight = document.getDouble("goalWeight")!!.toFloat()
            val lastWeight = document.getDouble("lastWeight")!!.toFloat()
            goalWeightTextView.text = getString(R.string.goalWeight,goalWeight)
            //현재 몸무게가 저번 몸무게 보다 크거나 같다면...
            Log.d(currentWeight.toString(),"22")
            if(currentWeight>=lastWeight){
                currentWeightTextView.text = getString(R.string.currentWeight,currentWeight,"+",currentWeight-lastWeight)
            }else{// 작다면 ...
                currentWeightTextView.text = getString(R.string.currentWeight,currentWeight,"-",lastWeight-currentWeight)
            }
        }



    }
    private fun isFirstTimeOpen(context: Context): Boolean {
        val prefName = "MyAppPreferences"
        val lastOpenDate = "lastOpenDate"
        val preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val lastOpenTime = preferences.getLong(lastOpenDate, 0)

        // 현재 날짜와 마지막으로 열었던 날짜를 비교하여 첫 실행 여부를 확인
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        return if (lastOpenTime == 0L){
            // 어플리케이션을 처음 실행하는 경우
            //유저 정보 set 만들기
            db.collection(uid)
                .document("userInformation")
                .set(userInform)

            // 마지막 열었던 날짜를 현재 날짜로 업데이트
            preferences.edit().putLong(lastOpenDate, currentTime).apply()
            true
        }else if(!isSameDay(currentTime, lastOpenTime)){
            //마지막으로 열었던 날짜가 오늘 날짜가 아닐경우

            //전체 날짜 += 1
            val ref = db.collection(uid).document("userInformation")
            ref.get()
                .addOnSuccessListener { document->
                    val days = document.getLong("days")!!
                    ref.update("days", days+1)
                }

            // 마지막 열었던 날짜를 현재 날짜로 업데이트
            preferences.edit().putLong(lastOpenDate, currentTime).apply()
            true
        } else {
            false // 이미 실행한 경우
        }
    }
    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = time1
        cal2.timeInMillis = time2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    private fun kcalProgress(){

    }
}