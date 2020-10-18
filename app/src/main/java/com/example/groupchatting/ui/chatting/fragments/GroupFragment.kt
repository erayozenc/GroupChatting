package com.example.groupchatting.ui.chatting.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupchatting.R
import com.example.groupchatting.adapters.MessagesAdapter
import com.example.groupchatting.models.Group
import com.example.groupchatting.ui.chatting.ChattingViewModel
import com.example.groupchatting.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_group.*

@AndroidEntryPoint
class GroupFragment : Fragment(R.layout.fragment_group) {

    private val viewModel : ChattingViewModel by viewModels()
    private val args : GroupFragmentArgs by navArgs()
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var group : Group

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group = args.group
        setupRecyclerView()
        viewModel.showMessages(group.groupId)

        clickListener()
        observeStatus()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object  : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_groupFragment_to_feedFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setupRecyclerView(){
        val currentUserUid = viewModel.getCurrentUserUid()
        messagesAdapter = MessagesAdapter(currentUserUid)
        rvMessages.apply {
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun clickListener(){
        sendButton.setOnClickListener {
            val message = etMessage.text.toString()
            if (message.isNotBlank()){
                viewModel.textMessage(message,group.groupId)
            }
            etMessage.setText("")
            viewModel.getUsersInGroup(group.groupId)
        }
    }

    private fun observeStatus(){
        viewModel.newMessageStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success->{
                    rvMessages.scrollToPosition(messagesAdapter.itemCount - 1)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.newMessageStatus.postValue(null)
        })

        viewModel.showMessagesStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success->{
                    it.data?.let {
                        messagesAdapter.differ.submitList(it)
                        rvMessages.smoothScrollToPosition(messagesAdapter.itemCount)
                    }
                }

                is Resource.Error->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading->{

                }
            }
            viewModel.showGroupsStatus.postValue(null)
        })

        viewModel.allUsersInGroupStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success->{
                    viewModel.sendUsersNotification(it.data!!)
                }
            }
        })
    }
}