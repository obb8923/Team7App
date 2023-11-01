package com.team7

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.team7.databinding.FragmentProfileBinding

class ProfileFragment:Fragment() {
    companion object{
        fun newInstance(userDisplayName: String?): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString("userDisplayName", userDisplayName)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var binding : FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val userDisplayNameTextView = binding.userDisplayName
        val userDisplayName = arguments?.getString("userDisplayName")
        userDisplayNameTextView.text = userDisplayName
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = FragmentProfileBinding.bind(requireView()) // binding 해제
    }
}