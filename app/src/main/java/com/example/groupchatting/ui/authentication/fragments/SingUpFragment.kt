package com.example.groupchatting.ui.authentication.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.example.groupchatting.util.Constants.GO_TO_GALLERY_CODE
import com.example.groupchatting.util.Constants.STORAGE_PERMISSION_CODE
import com.example.groupchatting.util.Permission
import com.example.groupchatting.util.Resource
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_signup.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class SingUpFragment : Fragment(R.layout.fragment_signup), EasyPermissions.PermissionCallbacks {

    private val viewModel : AuthViewModel by viewModels()
    private lateinit var uri : Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListeners()
        observeStatus()
    }

    private fun observeStatus(){
        viewModel.signUpStatus.observe(viewLifecycleOwner, Observer {
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

    private fun clickListeners(){
        ivRegisterProfilePhoto.setOnClickListener {
            requestPermission()
        }

        signupButton.setOnClickListener {
            var onesignalId = ""
            OneSignal.idsAvailable(OneSignal.IdsAvailableHandler { userId, registrationId ->
                onesignalId = userId
            })

            if(etSignupFirstName.text.isNotBlank() && etSignupLastName.text.isNotBlank()
                    && etSignupEmail.text.isNotBlank() && etSignupPassword.text.isNotBlank()
                    && etSignupPassword.text.toString().length >= 6){
                viewModel.singUp(
                        etSignupFirstName.text.toString(),
                        etSignupLastName.text.toString(),
                        etSignupEmail.text.toString(),
                        etSignupPassword.text.toString(),
                        onesignalId,
                        uri
                )
            }else if ( etSignupPassword.text.isNotBlank() && etSignupPassword.text.toString().length < 6){
                Toast.makeText(requireContext(),"Your password must be at least 6 characters!",Toast.LENGTH_SHORT).show()
            }

        }

        tvHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_singUpFragment_to_loginFragment)
        }
    }

    private fun requestPermission(){
        if (Permission.hasPermission(requireContext())){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,GO_TO_GALLERY_CODE)
        }else{
            EasyPermissions.requestPermissions(
                this,
                "You have to accept permission to upload a photo!",
                STORAGE_PERMISSION_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,GO_TO_GALLERY_CODE)
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

        if(resultCode == Activity.RESULT_OK && requestCode == GO_TO_GALLERY_CODE && data != null){
            data.data?.let {
                it?.let {
                    uri = it
                    ivRegisterProfilePhoto.setImageURI(it)
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