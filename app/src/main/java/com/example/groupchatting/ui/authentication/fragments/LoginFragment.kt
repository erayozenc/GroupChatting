package com.example.groupchatting.ui.authentication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.groupchatting.R
import com.example.groupchatting.ui.chatting.ChattingActivity
import com.example.groupchatting.ui.authentication.AuthViewModel
import com.example.groupchatting.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel : AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeStatus()
        clickListener()
    }

    private fun observeStatus(){
        viewModel.signInStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), ChattingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

                is Resource.Error ->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading ->{

                }
            }
        })
    }

    private fun clickListener(){
        signupButtonInLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_singUpFragment)
        }

        loginButton.setOnClickListener {
            viewModel.signIn(etLoginEmail.text.toString(),etLoginPassword.text.toString())
        }
    }
}