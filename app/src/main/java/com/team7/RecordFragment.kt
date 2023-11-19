package com.team7

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.team7.databinding.FragmentRecordBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
data class FoodRecordEntry(
    val kcal: Double=0.0,
    val carbohydrates: Double=0.0,
    val protein: Double=0.0,
    val fat: Double=0.0,
    val date: String=""
)
class RecordFragment : Fragment() {

    companion object {
        fun newInstance(selectedFoodItems: ArrayList<FoodItem>, userUid: String?): RecordFragment {
            val fragment = RecordFragment()
            val args = Bundle()
            args.putSerializable("selectedFoodItems", selectedFoodItems)
            args.putString("userUid", userUid)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: FragmentRecordBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var uid: String

    private val customSeekBars = ArrayList<CustomSeekBar>()
    private val currentDate = LocalDate.now() // 시간
    private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd") // 날짜 형식 포맷 지정
    private val formattedDate = currentDate.format(formatter)

    private var foodSet = hashMapOf(
        "kcal" to arrayListOf(0.0f, 0.0f, 0.0f, 0.0f),
        "carbohydrates" to arrayListOf(0.0f, 0.0f, 0.0f, 0.0f),
        "protein" to arrayListOf(0.0f, 0.0f, 0.0f, 0.0f),
        "fat" to arrayListOf(0.0f, 0.0f, 0.0f, 0.0f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        uid = arguments?.getString("userUid") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun FoodRecordEntry.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "kcal" to kcal,
            "carbohydrates" to carbohydrates,
            "protein" to protein,
            "fat" to fat,
            "date" to date
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            args.getSerializable("selectedFoodItems")?.let {
                val selectedFoodItems = it as ArrayList<FoodItem>
                createCustomSeekBars(view.findViewById(R.id.customSeekBarContainer), selectedFoodItems)
            }
        }
        val saveButton: Button = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val totalValues = hashMapOf(
                "kcal" to 0.0,
                "carbohydrates" to 0.0,
                "protein" to 0.0,
                "fat" to 0.0
            )

            customSeekBars.forEachIndexed { _, customSeekBar ->
                val percent = customSeekBar.percentage
                val foodItem = customSeekBar.foodItem

                totalValues["kcal"] = totalValues["kcal"]!! + (foodItem.calories * percent / 100)
                totalValues["carbohydrates"] = totalValues["carbohydrates"]!! + (foodItem.carbs * percent / 100)
                totalValues["protein"] = totalValues["protein"]!! + (foodItem.protein * percent / 100)
                totalValues["fat"] = totalValues["fat"]!! + (foodItem.fat * percent / 100)
            }

            // 새로운 Firestore 문서 참조 생성
            val foodRecordDocRef = db.collection(uid)
                .document("foodRecord")
                .collection("foodRecords")
                .document()  // 자동으로 생성된 문서 ID 사용

            // FoodRecordEntry 객체 생성
            val foodEntry = FoodRecordEntry(
                kcal = totalValues["kcal"]!!,
                carbohydrates = totalValues["carbohydrates"]!!,
                protein = totalValues["protein"]!!,
                fat = totalValues["fat"]!!,
                date = formattedDate
            )


            // Firestore에 데이터 저장
            foodRecordDocRef.set(foodEntry)
                .addOnFailureListener { e ->
                    Log.e("FireBase", "Error writing document", e)
                }
            navigateBackToMainRecordFragment()
        }




    }
    private fun navigateBackToMainRecordFragment() {
        // 모든 프래그먼트 스택을 비우고 메인 레코드 프래그먼트로 돌아가기
        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        // 현재 활성화된 프래그먼트 찾기
        val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.container)

        // 현재 프래그먼트 새로고침
        currentFragment?.let {
            requireActivity().supportFragmentManager.beginTransaction()
                .detach(it)
                .attach(it)
                .commit()
        }
    }

    private fun createCustomSeekBars(container: LinearLayout, items: ArrayList<FoodItem>) {
        container.removeAllViews()
        customSeekBars.clear()
        items.forEachIndexed { index, item ->
            context?.let { ctx ->
                val seekBar = CustomSeekBar(ctx)
                val params = LinearLayout.LayoutParams(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, ctx.resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, ctx.resources.displayMetrics).toInt()
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    if (index != items.size - 1) {
                        setMargins(0, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, ctx.resources.displayMetrics).toInt(), 0)
                    }
                }

                seekBar.layoutParams = params
                seekBar.setIconDrawableRes(ctx.resources.getIdentifier(item.iconName, "drawable", ctx.packageName))
                seekBar.foodItem=item
                container.addView(seekBar)
                customSeekBars.add(seekBar)
            }
        }
    }

}
