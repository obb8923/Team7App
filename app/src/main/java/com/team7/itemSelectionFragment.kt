package com.team7

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.io.Serializable

data class FoodItem(
    val category: String,
    val iconName: String, // 아이콘 이름 추가
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double
):Serializable

class IconSelectionFragment : Fragment() {

    private lateinit var iconGridView: GridView
    private lateinit var selectedIconsLayout: LinearLayout
    private val allFoodItems = ArrayList<FoodItem>()
    private fun readCsvFile(context: Context) {
        val assetManager = context.assets
        val inputStream = assetManager.open("food_info.csv")
        val bufferedReader = inputStream.bufferedReader()

        bufferedReader.useLines { lines ->
            lines.drop(1).forEach { line ->
                val tokens = line.split(",")

                val category = tokens[0] // 분류
                val iconName = tokens[6].toString()//아이콘
                val calories = tokens[2].toDouble() // 칼로리
                val carbs = tokens[3].toDouble() // 탄수화물
                val protein = tokens[4].toDouble() // 단백질
                val fat = tokens[5].toDouble() // 지방

                val foodItem = FoodItem(category, iconName, calories, carbs, protein, fat)
                allFoodItems.add(foodItem)
            }
        }
    }

    private val iconNames = arrayOf("빵", "채소", "과일", "밥", "고기")
    private val selectedItems = ArrayList<FoodItem>()

    companion object {
        fun newInstance(userUid: String?): IconSelectionFragment {
            val fragment = IconSelectionFragment()
            val args = Bundle()
            args.putString("userUid", userUid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_icon_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { readCsvFile(it) }

        iconGridView = view.findViewById(R.id.iconGridView)
        selectedIconsLayout = view.findViewById(R.id.selectedIconsLayout)

        iconGridView.adapter = object : ArrayAdapter<FoodItem>(requireContext(), R.layout.icon_item, allFoodItems) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var convView = convertView
                if (convView == null) {
                    convView = LayoutInflater.from(context).inflate(R.layout.icon_item, parent, false)
                }

                val iconImageView: ImageView = convView!!.findViewById(R.id.iconImageView)
                val iconNameTextView: TextView = convView.findViewById(R.id.iconNameTextView)

                val currentItem = allFoodItems[position]
                val resourceId = context.resources.getIdentifier(currentItem.iconName, "drawable", context.packageName)
                iconImageView.setImageResource(resourceId)
                iconNameTextView.text = currentItem.category

                return convView
            }
        }


        iconGridView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = allFoodItems[position]
            Log.d("selectedItem", "onViewCreated: "+selectedItem.iconName)
            selectedItems.add(selectedItem)
            updateSelectedIcons()
        }

        val proceedButton: Button = view.findViewById(R.id.proceedButton)
        proceedButton.setOnClickListener {
            val recordFragment = RecordFragment.newInstance(selectedItems, arguments?.getString("userUid"))
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, recordFragment)
                .addToBackStack(null)
                .commit()
        }

        updateSelectedIcons()
    }

    private fun updateSelectedIcons() {
        val diff = selectedItems.size - selectedIconsLayout.childCount

        if (diff > 0) {
            for (i in 0 until diff) {
                val iconImageView = ImageView(context)
                val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
                val params = LinearLayout.LayoutParams(size, size)
                iconImageView.layoutParams = params

                val indexToAdd = selectedItems.size - diff + i
                iconImageView.tag = indexToAdd

                iconImageView.setOnClickListener { v ->
                    val idxToRemove = v.tag as Int
                    selectedItems.removeAt(idxToRemove)
                    updateSelectedIcons()
                }

                selectedIconsLayout.addView(iconImageView)
            }
        } else if (diff < 0) {
            for (i in 0 until -diff) {
                selectedIconsLayout.removeViewAt(selectedIconsLayout.childCount - 1)
            }
        }

        for (i in selectedItems.indices) {
            val iconImageView = selectedIconsLayout.getChildAt(i) as ImageView
            val resourceId = requireContext().resources.getIdentifier(selectedItems[i].iconName, "drawable", requireContext().packageName)
            iconImageView.setImageResource(resourceId)
        }
    }
}
