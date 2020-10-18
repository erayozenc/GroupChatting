package com.example.groupchatting.ui.chatting.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.groupchatting.R
import com.example.groupchatting.ui.authentication.AuthActivity
import com.example.groupchatting.ui.chatting.ChattingViewModel
import com.example.groupchatting.util.Constants
import com.example.groupchatting.util.Permission
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_group.*
import kotlinx.android.synthetic.main.fragment_profile.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile),EasyPermissions.PermissionCallbacks {

    private val viewModel : ChattingViewModel by viewModels()

    private lateinit var uri : Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signOutProfile ->{
                viewModel.signOut()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCurrentUser()

        clickListeners()
        observeStatus()
    }

    private fun clickListeners(){
        ivProfileProfilePhoto.setOnClickListener {
            requestPermission()
        }

        saveButton.setOnClickListener {
            if (etProfilePassword.text.isNotBlank() && etProfilePassword.text.toString().length >= 6){
                val newPassword = etProfilePassword.text.toString()
                viewModel.changePassword(newPassword)
                findNavController().navigate(R.id.action_profileFragment_to_feedFragment)
                Toast.makeText(requireContext(),"Your password have succesfully changed.",Toast.LENGTH_SHORT).show()
            }else if (etProfilePassword.text.isNotBlank() && etProfilePassword.text.toString().length in 1..5){
                Toast.makeText(requireContext(),"Your new password must be at least 6 characters!",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"Please enter your new password to change!",Toast.LENGTH_SHORT).show()
            }
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
                    viewModel.changeProfilePhoto(it)
                    ivProfileProfilePhoto.setImageURI(it)
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

    private fun observeStatus(){
        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it.profilePhoto).into(ivProfileProfilePhoto)
            tvUserName.text = it.firstName + " " + it.lastName
        })
    }
}