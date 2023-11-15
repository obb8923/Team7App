package com.team7

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team7.databinding.FragmentMoreBinding

class MoreFragment:Fragment() {
    companion object{
        fun newInstance():MoreFragment{
            return MoreFragment()
        }
    }
    private lateinit var binding: FragmentMoreBinding




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
        noticeButton()
    }
    private fun alertLogout(){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logoutConfirm)
            .setMessage("")
            .setPositiveButton(R.string.logout, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    Firebase.auth.signOut()
                    val intent = Intent(context,MainActivity::class.java)
                    startActivity(intent)
                }
            })
            .setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                }
            })
            .create()
            .show()
    }

    private fun logoutButton(){
        binding.moreLogoutButton.setOnClickListener {
            alertLogout()
        }
    }
    private fun noticeButton(){
        binding.moreNoticeButton.setOnClickListener {
            val intent = Intent(getActivity(),NoticeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = FragmentMoreBinding.bind(requireView()) // binding 해제
    }
}