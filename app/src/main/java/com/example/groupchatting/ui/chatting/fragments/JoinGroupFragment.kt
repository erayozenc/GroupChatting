package com.example.groupchatting.ui.chatting.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.groupchatting.R
import com.example.groupchatting.ui.chatting.ChattingViewModel
import com.example.groupchatting.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_join_group.*

@AndroidEntryPoint
class JoinGroupFragment : Fragment(R.layout.fragment_join_group) {

    private val viewModel : ChattingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListener()
        observeStatus()
    }

    private fun observeStatus(){
        viewModel.joinGroupStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    val group = it.data
                    group?.let {
                        viewModel.saveJoinedUserToGroup(it.groupId)
                        val bundle = Bundle().apply {
                            putParcelable("group",it)
                        }
                        findNavController().navigate(R.id.action_joinGroupFragment_to_groupFragment,bundle)
                    }
                }

                is Resource.Error ->{
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading ->{
                }
            }
            viewModel.joinGroupStatus.postValue(null)
        })
    }

    private fun clickListener(){
        joinButton.setOnClickListener {
            if(etJoinGroupName.text.isNotBlank() && etJoinGroupPassword.text.isNotBlank()){
                viewModel.joinGroup(etJoinGroupName.text.toString(),etJoinGroupPassword.text.toString())
            }
        }
        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_joinGroupFragment_to_feedFragment)
        }
    }
}