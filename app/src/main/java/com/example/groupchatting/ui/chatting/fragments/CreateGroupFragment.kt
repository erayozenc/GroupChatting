package com.example.groupchatting.ui.chatting.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.groupchatting.R
import com.example.groupchatting.ui.chatting.ChattingViewModel
import com.example.groupchatting.util.Constants
import com.example.groupchatting.util.Permission
import com.example.groupchatting.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_group.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class CreateGroupFragment : Fragment(R.layout.fragment_create_group),EasyPermissions.PermissionCallbacks {

    private val viewModel : ChattingViewModel by activityViewModels()
    private lateinit var uri : Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListener()
        observeStatus()
    }

    private fun observeStatus(){
        viewModel.createGroupStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    val group = it.data
                    group?.let {
                        val bundle = Bundle().apply {
                            putParcelable("group",it)
                        }
                        findNavController().navigate(R.id.action_createGroupFragment_to_groupFragment,bundle)
                    }
                }

                is Resource.Error ->{
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading ->{
                }
            }
            viewModel.createGroupStatus.postValue(null)
        })
    }

    private fun clickListener(){
        ivCreateGroupIcon.setOnClickListener {
            requestPermission()
        }

        createButton.setOnClickListener {
            if(etCreateGroupName.text.isNotBlank() && etCreateGroupPassword.text.isNotBlank()){
                viewModel.createGroup(etCreateGroupName.text.toString(),etCreateGroupPassword.text.toString(),uri)
            }
        }
        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_createGroupFragment_to_feedFragment)
        }
    }

    private fun requestPermission(){
        if (Permission.hasPermission(requireContext())){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, Constants.GO_TO_GALLERY_CODE)
        }else{
            EasyPermissions.requestPermissions(
                    this,
                    "You have to accept permission to upload a photo!",
                    Constants.STORAGE_PERMISSION_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.GO_TO_GALLERY_CODE)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == Constants.GO_TO_GALLERY_CODE && data != null){
            data.data?.let {
                it?.let {
                    uri = it
                    ivCreateGroupIcon.setImageURI(it)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}