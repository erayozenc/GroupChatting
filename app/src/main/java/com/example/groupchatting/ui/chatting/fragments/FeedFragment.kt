package com.example.groupchatting.ui.chatting.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupchatting.R
import com.example.groupchatting.adapters.GroupsAdapter
import com.example.groupchatting.ui.authentication.AuthActivity
import com.example.groupchatting.ui.chatting.ChattingViewModel
import com.example.groupchatting.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_feed.*

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val viewModel : ChattingViewModel by viewModels()
    private lateinit var groupsAdapter: GroupsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.showGroups()

        observeStatus()
    }

    private fun setupRecyclerView(){
        groupsAdapter = GroupsAdapter()
        rvGroups.apply {
            adapter = groupsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeStatus(){
        viewModel.showGroupsStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    it.data?.let {
                        groupsAdapter.differ.submitList(it)
                    }
                }

                is Resource.Error ->{
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading ->{

                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.createGroupDialog->{
                findNavController().navigate(R.id.action_feedFragment_to_createGroupFragment)
            }
            R.id.joinGroupDialog->{
                findNavController().navigate(R.id.action_feedFragment_to_joinGroupFragment)
            }
            R.id.signOut->{
                viewModel.signOut()
                val intent = Intent(requireContext(),AuthActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            R.id.profileFragment->{
                findNavController().navigate(R.id.action_feedFragment_to_profileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}