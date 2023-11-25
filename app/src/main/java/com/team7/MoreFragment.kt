package com.team7

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team7.databinding.FragmentMoreBinding

class MoreFragment:Fragment() {
    companion object{
        fun newInstance():MoreFragment{
            return MoreFragment()
        }
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInAccount:GoogleSignInClient
    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        googleSignInAccount = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
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
                    auth.signOut()
                    googleSignInAccount.signOut().addOnSuccessListener {
                        val intent = Intent(context,MainActivity::class.java)
                        startActivity(intent)
                        //finish()
                        requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .remove(this@MoreFragment)
                            .commit()

                    }

                }
            })
            .setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    //아무것도 안하기
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