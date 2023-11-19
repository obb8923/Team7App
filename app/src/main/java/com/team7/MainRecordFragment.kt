package com.team7

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.team7.databinding.FragmentRecordMainBinding



class MainRecordFragment : Fragment() {
    private lateinit var binding: FragmentRecordMainBinding
    private lateinit var uid: String
    private val foodRecords = mutableListOf<FoodRecordEntry>()
    private lateinit var recordAdapter: RecordAdapter
    private var lastVisibleDocumentSnapshot: DocumentSnapshot? = null
    private var isLoading = false
    private lateinit var fabIconSelection: FloatingActionButton
    companion object {
        fun newInstance(userUid: String?): MainRecordFragment {
            val fragment = MainRecordFragment()
            val args = Bundle()
            args.putString("userUid", userUid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = arguments?.getString("userUid") ?: ""
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecordMainBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadFoodRecords(initialLoad = true)
        return binding.root
    }

    private fun setupRecyclerView() {
        recordAdapter = RecordAdapter(foodRecords)
        binding.recordRecyclerView.adapter = recordAdapter
        binding.recordRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.recordRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 스크롤이 끝에 도달했는지
                if (!recyclerView.canScrollVertically(1)) {
                    // 더 로드할 데이터가 있는지
                    if (lastVisibleDocumentSnapshot != null && !isLoading) {
                        loadFoodRecords(initialLoad = false)
                    }
                }
            }
        })
    }
    private fun loadFoodRecords(initialLoad: Boolean) {
        if (isLoading) return
        isLoading = true

        var query = FirebaseFirestore.getInstance()
            .collection(uid)
            .document("foodRecord")
            .collection("foodRecords")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(15)

        if (!initialLoad && lastVisibleDocumentSnapshot != null) {
            query = query.startAfter(lastVisibleDocumentSnapshot)
        }

        query.get().addOnSuccessListener { documents ->
            if (initialLoad) {
                foodRecords.clear()
            }

            // 더 이상 불러올 데이터가 없을 경우 처리
            if (documents.isEmpty) {
                isLoading = false
                lastVisibleDocumentSnapshot = null
                return@addOnSuccessListener
            }

            val newRecords = documents.documents.mapNotNull { it.toObject(FoodRecordEntry::class.java) }
            val groupedRecords = newRecords.groupBy { it.date }.mapValues { (_, records) ->
                records.reduce { acc, record ->
                    FoodRecordEntry(
                        kcal = acc.kcal + record.kcal,
                        carbohydrates = acc.carbohydrates + record.carbohydrates,
                        protein = acc.protein + record.protein,
                        fat = acc.fat + record.fat,
                        date = acc.date
                    )
                }
            }.values.toList()

            foodRecords.addAll(groupedRecords)
            recordAdapter.notifyDataSetChanged()

            // 결과 개수가 15개 미만이면 더 이상 불러올 데이터가 없음을 의미
            if (documents.size() < 15) {
                lastVisibleDocumentSnapshot = null
            } else {
                lastVisibleDocumentSnapshot = documents.documents.lastOrNull()
            }

            isLoading = false
        }.addOnFailureListener {
            isLoading = false
            Log.e("FireBase", "loadFoodRecords: ", it)
        }
    }

//개별로 불러오는 코드
//    private fun loadFoodRecords(initialLoad: Boolean) {
//        if (isLoading) return
//        isLoading = true
//
//        var query = FirebaseFirestore.getInstance()
//            .collection(uid)
//            .document("foodRecord")
//            .collection("foodRecords")
//            .orderBy("date", Query.Direction.DESCENDING)
//            .limit(15)
//
//        if (!initialLoad && lastVisibleDocumentSnapshot != null) {
//            query = query.startAfter(lastVisibleDocumentSnapshot)
//        }
//
//        query.get().addOnSuccessListener { documents ->
//            if (initialLoad) {
//                foodRecords.clear()
//            }
//
//            // 결과가 비어있으면 로딩 중지
//            if (documents.isEmpty) {
//                isLoading = false
//                lastVisibleDocumentSnapshot = null
//                return@addOnSuccessListener
//            }
//
//            for (document in documents) {
//                val foodRecord = document.toObject(FoodRecordEntry::class.java)
//                foodRecords.add(foodRecord)
//            }
//
//            recordAdapter.notifyDataSetChanged()
//
//            // 결과 개수가 15개 미만이면 더 이상 불러올 데이터가 없다
//            if (documents.size() < 15) {
//                lastVisibleDocumentSnapshot = null
//            } else {
//                lastVisibleDocumentSnapshot = documents.documents.lastOrNull()
//            }
//
//            isLoading = false
//        }.addOnFailureListener {
//            isLoading = false
//            Log.e("FireBase", "loadFoodRecords: ", it)
//        }
//    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadFoodRecords(initialLoad = true)
        fabIconSelection = view.findViewById(R.id.fabIconSelection)
        activity?.supportFragmentManager?.addOnBackStackChangedListener {
            val currentFragment = activity?.supportFragmentManager?.findFragmentById(R.id.container)
            if (currentFragment is MainRecordFragment) {
                fabIconSelection.show()
            } else {
                fabIconSelection.hide()
            }
        }
        fabIconSelection.setOnClickListener {
            navigateToIconSelectionFragment()
        }
    }
    private fun navigateToIconSelectionFragment() {
        val iconSelectionFragment = IconSelectionFragment.newInstance(arguments?.getString("userUid"))
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, iconSelectionFragment)
            .addToBackStack(null)
            .commit()
    }


    class RecordAdapter(private val records: List<FoodRecordEntry>) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_record, parent, false)
            return RecordViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
            val record = records[position]
            holder.bind(record)
        }

        override fun getItemCount(): Int = records.size

        class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val dateTextView: TextView = view.findViewById(R.id.dateTextView)
            private val totalCaloriesTextView: TextView = view.findViewById(R.id.totalCaloriesTextView)
            private val totalCarbsTextView: TextView = view.findViewById(R.id.totalCarbsTextView)
            private val totalProteinTextView: TextView = view.findViewById(R.id.totalProteinTextView)
            private val totalFatTextView: TextView = view.findViewById(R.id.totalFatTextView)

            fun bind(record: FoodRecordEntry) {
                dateTextView.text = record.date
                totalCaloriesTextView.text = "칼로리: ${record.kcal.toInt()}"
                totalCarbsTextView.text = "탄수화물: ${record.carbohydrates.toInt()}"
                totalProteinTextView.text = "단백질: ${record.protein.toInt()}"
                totalFatTextView.text = "지방: ${record.fat.toInt()}"
            }
        }
    }

}
