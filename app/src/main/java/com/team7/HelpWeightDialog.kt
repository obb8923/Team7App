package layout

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.team7.R

class HelpWeightDialog(context: Context):Dialog(context) {
    companion object {
        fun show(context: Context,h:Float,w:Int) {
            // 다이얼로그 빌더 생성
            val builder = AlertDialog.Builder(context)
            // XML 레이아웃을 이용하여 다이얼로그 뷰 설정
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_help_weight, null)
            val text = dialogView.findViewById<TextView>(R.id.normalWeight)
            text.text = context.getString(R.string.myNormalWeight,((h*h/10000*w*10).toInt().toFloat()/10).toString())
            builder.setView(dialogView)
            // 다이얼로그 버튼 클릭 이벤트 설정
            builder.setPositiveButton("확인") { dialog, which ->
                // 아래는 확인 버튼 클릭 시 수행할 동작
                // 정보 수정 및 표시
            }
            // 다이얼로그 생성 및 표시
            val dialog = builder.create()
            dialog.show()
        }
    }
}