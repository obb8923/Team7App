package com.team7

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team7.databinding.FragmentMoreBinding
import com.team7.databinding.FragmentProfileBinding

class MoreFragment:Fragment() {
    companion object{
        fun newInstance():MoreFragment{
            return MoreFragment()
        }
    }
    private lateinit var binding: FragmentMoreBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        logoutButton()
    }
    private fun alertLogout(){
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃 하시겠습니까?")
            .setMessage("")
            .setPositiveButton("로그아웃", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    Firebase.auth.signOut()
                    val intent = Intent(context,MainActivity::class.java)
                    startActivity(intent)
                }
            })
            .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                }
            })
            .create()
            .show()
    }

    private fun logoutButton(){
        binding.moreLogoutButton.setOnClickListener {
            Log.d("MyTag", "클릭로그아웃")
            alertLogout()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = FragmentMoreBinding.bind(requireView()) // binding 해제
    }
}