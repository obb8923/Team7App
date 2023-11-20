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
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.integrity.p
import com.google.android.play.integrity.internal.c
import com.google.android.play.integrity.internal.f
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team7.databinding.FragmentProfileBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ProfileFragment:Fragment() {
    companion object{
        fun newInstance(userDisplayName: String?,userUid:String?,ds:String?,wds:String?,dds:String?): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString("userDisplayName", userDisplayName)
            args.putString("userUid", userUid)
            args.putString("ds",ds)
            args.putString("wds",wds)
            args.putString("dds",dds)
            fragment.arguments = args
            return fragment
        }
    }
    private var toast: Toast? = null
    private lateinit var binding : FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var uid : String
    private lateinit var userDisplayName :String

    private var days:String? ="null"
    private var workoutDays:String? ="null"
    private var dietDays:String? ="null"


    private val currentDate = LocalDate.now()//시간
    private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd") //날짜 형식 포맷 지정
    private val formattedDate = currentDate.format(formatter)

    private var userInform = hashMapOf(
        "name" to "null",
        "gender" to "",
        "age" to 0,
        "height" to 0.0f,
        "weight" to 0.0f,
        "goalWeight" to 0.0f,
        "lastWeight" to 0.0f,
        "lastWorkout" to "",
        "days" to 1,
        "workoutDay" to 0,
        "dietDay" to 0
    )

    private val foodRecords = mutableListOf<FoodRecordEntry>()
    private lateinit var recordAdapter: MainRecordFragment.RecordAdapter
    private var lastVisibleDocumentSnapshot: DocumentSnapshot? = null
    private var isLoading = false
    private lateinit var fabIconSelection: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        userDisplayName=arguments?.getString("userDisplayName")!!
        uid =arguments?.getString("userUid")!!
        days = arguments?.getString("ds")!!
        workoutDays = arguments?.getString("wds")!!
        dietDays = arguments?.getString("dds")!!

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
        //makeSet()
        layoutValueUpdate()
        myNutrition()

    }
    override fun onResume() {
        super.onResume()
        val context = requireContext()
        profileName()
        isFirstTimeOpen(context)
        correctionButton()
        workoutCheck()
    }
    override fun onPause() {
        super.onPause()
        toast?.cancel()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = FragmentProfileBinding.bind(requireView()) // binding 해제
    }
    private fun profileName(){
        val userName = binding.userName
        userName.text =userDisplayName
    }
    private fun layoutValueUpdate(){
        daysUpdate()
        weightUpdate()
    }
    private fun daysUpdate(){
        // 전체|운동|식단
        val allDay = binding.allDay
        val workoutDay = binding.workoutDay
        val dietDay = binding.dietDay
        //update
        if(days=="null") {
            val ref = db.collection(uid).document("userInformation")
            ref.get()
                .addOnSuccessListener { document ->
                    days = (document.getLong("days") ?: 0).toString() // Use 0 if "days" is null
                    workoutDays =
                        (document.getLong("workoutDay") ?: 0).toString() // Use 0 if "days" is null
                    dietDays =
                        (document.getLong("dietDay") ?: 0).toString() // Use 0 if "days" is null
                }
        }
        allDay.text = getString(R.string.nullDays, days)
        workoutDay.text = getString(R.string.nullDays, workoutDays)
        dietDay.text = getString(R.string.nullDays, dietDays)
    }

    private fun correctionButton(){
        val button = binding.profileProfileButton
        button.setOnClickListener {
            // 다이얼로그 빌더 생성
            val builder = AlertDialog.Builder(context)

            // XML 레이아웃을 이용하여 다이얼로그 뷰 설정
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.weight_dialog, null)
            builder.setView(dialogView)
            // 다이얼로그 버튼 클릭 이벤트 설정
            builder.setPositiveButton("확인") { dialog, which ->
                // 아래는 확인 버튼 클릭 시 수행할 동작
                // 정보 수정 및 표시
                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.select_sex)
                var selectedSex:String ="male"
                val editText0 = dialogView.findViewById<EditText>(R.id.age)
                val editText1 = dialogView.findViewById<EditText>(R.id.height)
                val editText2 = dialogView.findViewById<EditText>(R.id.goal_weight)
                val editText3 = dialogView.findViewById<EditText>(R.id.current_weight)
                val w0 = editText0.text.toString().toIntOrNull() ?: 0
                val w1 = editText1.text.toString().toFloatOrNull() ?: 0.0f
                val w2 = editText2.text.toString().toFloatOrNull() ?: 0.0f
                val w3 = editText3.text.toString().toFloatOrNull() ?: 0.0f
                radioGroup.setOnCheckedChangeListener{group,checkedId->
                    when(checkedId){
                        R.id.male->selectedSex = "male"
                        R.id.female->selectedSex="female"
                    }
                }
                if(w0>0&&w1>0.0f&&w2>0.0f&&w3>0.0f) {
                    //유저 정보 변경 - 몸무게
                    val ref = db.collection(uid).document("userInformation")
                    ref .get().addOnSuccessListener { document->
                        ref.update("lastWeight", document.getDouble("weight")!!.toFloat())
                    }
                    ref.update(
                        mapOf(
                            "gender" to selectedSex,
                            "age" to w0,
                            "height" to w1,
                            "goalWeight" to w2,
                            "weight" to w3
                        )
                    )
                    weightUpdate()
                }
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
            if(currentWeight>=lastWeight){
                currentWeightTextView.text = getString(R.string.currentWeight,currentWeight,"+",currentWeight-lastWeight)
            }else{// 작다면 ...
                currentWeightTextView.text = getString(R.string.currentWeight,currentWeight,"-",lastWeight-currentWeight)
            }
        }
    }
    private fun makeSet(){
        val ref = db.collection(uid)
            .document("userInformation")
        ref.set(userInform)
    }
/*
    private fun firstInput(){
        //유저 정보 set 만들기
        val ref = db.collection(uid)
            .document("userInformation")
        ref.set(userInform)
        //성별, 나이 , 키 ,몸무게 입력 dialog 띄우기
        // 다이얼로그 빌더 생성
        val builder = AlertDialog.Builder(context)

        // XML 레이아웃을 이용하여 다이얼로그 뷰 설정
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.first_input, null)
        builder.setView(dialogView)
        // 다이얼로그 버튼 클릭 이벤트 설정
        builder.setPositiveButton("확인") { dialog, which ->
            // 확인 버튼 클릭 시 수행할 동작
            val radioGroup = dialogView.findViewById<RadioGroup>(R.id.select_sex)
            var selectedSex:String = "female"
            val age = dialogView.findViewById<EditText>(R.id.age)
            val height = dialogView.findViewById<EditText>(R.id.height)
            val weight = dialogView.findViewById<EditText>(R.id.weight)
            radioGroup.setOnCheckedChangeListener{group,checkedId->
                when(checkedId){
                    R.id.male->selectedSex = "male"
                    R.id.female->selectedSex="female"
                }
            }
            val ageValue = age.text.toString().toIntOrNull() ?: 0
            val heightValue = height.text.toString().toFloatOrNull() ?: 0.0f
            val weightValue = weight.text.toString().toFloatOrNull() ?: 0.0f
            ref.update(
                mapOf(
                    "gender" to selectedSex,
                    "age" to ageValue,
                    "height" to heightValue,
                    "weight" to weightValue
                )
            )
        }
        // 다이얼로그 생성 및 표시
        val dialog = builder.create()
        dialog.show()
    }
*/

    private fun isFirstTimeOpen(context: Context): Boolean {
        val prefName = "MyAppPreferences"
        val lastOpenDate = "lastOpenDate"
        val preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        var lastOpenTime = preferences.getLong(lastOpenDate, 0)
        // 현재 날짜와 마지막으로 열었던 날짜를 비교하여 첫 실행 여부를 확인
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        return if (lastOpenTime == 0L){
            // 어플리케이션을 처음 실행하는 경우
            //유저 정보 set 만들기
            val ref = db.collection(uid)
                .document("userInformation")
            ref.set(userInform)
            //1700367445507
            //layoutValueUpdate()
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
    private fun myNutrition(){
        var c = 0.0
        var p = 0.0
        var f = 0.0
        var k = 0.0
        var query = FirebaseFirestore.getInstance()
            .collection(uid)
            .document("foodRecord")
            .collection("foodRecords")
            .whereEqualTo("date",formattedDate)

        query.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                f+= document.getDouble("fat")!!
                p+= document.getDouble("protein")!!
                c+= document.getDouble("carbohydrates")!!
                k+= document.getDouble("kcal")!!
            }
            val cp  = roundDigit(c,0).toInt()
            val pp = roundDigit(p,0).toInt()
            val fp = roundDigit(f,0).toInt()
            val kp = roundDigit(k,0).toInt()
            progressMax(kp,cp,pp,fp)
        }
    }

    private fun progressMax(kp:Int,cp:Int,pp:Int,fp:Int){
        val pBarCal = binding.progressBarCalorie
        val pBarCarbohydrate = binding.progressBarCarbohydrate
        val pBarProtein = binding.progressBarProtein
        val pBarFat = binding.progressBarFat
        val textCalorie = binding.calText
        val textCarbohydrate = binding.carboText
        val textProtein = binding.protText
        val textFat = binding.fatText
        //권장 칼로리 계산
        val ref = db.collection(uid).document("userInformation")
        ref.get().addOnSuccessListener { document->
            val h1 = document.getDouble("height")!!.toFloat()
            val w1 = document.getDouble("weight")!!.toFloat()
            val sex =document.getString("gender")!!
            var sexN=22
            if(sex!="male")sexN =21
            //남자는22여자는21을 곱한다
            //키, 몸무게 값이 입력이 되어 있다면 적정 체중, 적정 칼로리 계산
            if(h1>0.0f&&w1>0.0f){
                var normalWeight = h1*h1*sexN/10000
                //177*177*22/10000
                //69.7048
                normalWeight = (((normalWeight* 10000).toInt() / 1000).toFloat()/10)
                //69.7
                //3대영양소 탄단지 5:3:2
                val p = roundDigit((normalWeight*30)*0.5/4,0).toInt()
                val c = roundDigit((normalWeight*30)*0.3/4,0).toInt()
                val f = roundDigit((normalWeight*30)*0.2/9,0).toInt()
                val k =(normalWeight*30).toInt()//697*30
                //권장 영양소 max 값 Update
                pBarCal.max = k
                pBarCarbohydrate.max = c
                pBarProtein.max=p
                pBarFat.max = f
                //진행도Update
                pBarCarbohydrate.progress = cp
                pBarProtein.progress = pp
                pBarFat.progress = fp
                pBarCal.progress = kp
                //오늘 먹은 영양소
                val kps = kp.toString()
                val pps = pp.toString()
                val cps = cp.toString()
                val fps = fp.toString()
                //진행도 percent
                val kPercent = roundDigit((kp.toDouble()/k*100),1).toString()
                val pPercent = roundDigit((pp.toDouble()/p*100),1).toString()
                val cPercent = roundDigit((cp.toDouble()/c*100),1).toString()
                val fPercent = roundDigit((fp.toDouble()/f*100),1).toString()

                //Text 나타내기
                textCalorie.text = getString(R.string.progressText,"칼로리",kPercent+"%",kps,(normalWeight*30).toInt().toString())
                textCarbohydrate.text =getString(R.string.progressText,"탄수화물",cPercent+"%",cps,c.toString())
                textProtein.text = getString(R.string.progressText,"단백질",pPercent+"%",pps,p.toString())
                textFat.text = getString(R.string.progressText,"지방",fPercent+"%",fps,f.toString())
            }
        }
    }
    fun roundDigit(number : Double, digits : Int): Double { //소수점 반올림 함수
        return Math.round(number * Math.pow(10.0, digits.toDouble())) / Math.pow(10.0, digits.toDouble())
    }
    private fun workoutCheck(){
        val b = binding.workoutCheckButton
        var lastWorkout=""
        var days=0
        val ref = db.collection(uid).document("userInformation")
        ref.get().addOnSuccessListener {document->
            lastWorkout = document.getString("lastWorkout")!!
            days = document.getLong("workoutDay")!!.toInt()
        }
        b.setOnClickListener {
            if(lastWorkout!=formattedDate){

                ref.update("lastWorkout",formattedDate)
                ref.update("workoutDay",days+1)
            }else{
                if(toast==null){
                    toast = Toast.makeText(getContext(),"버튼을 이미 눌렀습니다.",Toast.LENGTH_SHORT)
                    toast?.show()
                }
            }
            daysUpdate()
        }
    }
}