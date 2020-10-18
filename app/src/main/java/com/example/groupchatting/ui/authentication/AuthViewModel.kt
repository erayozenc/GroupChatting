package com.example.groupchatting.ui.authentication

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupchatting.models.User
import com.example.groupchatting.repository.MainRepository
import com.example.groupchatting.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel(){

    val signUpStatus = MutableLiveData<Resource<Any>>()
    val signInStatus = MutableLiveData<Resource<Any>>()

    fun singUp(
        firstName : String,
        lastName : String,
        email : String,
        password : String,
        oneSignalId : String,
        uri : Uri
    ) = viewModelScope.launch {
        signUpStatus.postValue(Resource.Loading())
        if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()){
            try {
                repository.signup(email,password)
                repository.uploadProfilePhoto(uri)
                val photoUrl = repository.getProfilePhotoUrl()
                val uid = repository.getUid()
                val user = User(uid!!,firstName,lastName,email,photoUrl,oneSignalId)
                repository.saveUser(user)
                signUpStatus.postValue(Resource.Success("","Succesfully signed-up!"))
            }catch (e : Exception){
                signUpStatus.postValue(Resource.Error(e.message))
            }
        }else{
            signUpStatus.postValue(Resource.Error("Please fill all the blanks!"))
        }
    }
    
    fun signIn(email : String,password : String) = viewModelScope.launch {
        signInStatus.postValue(Resource.Loading())
        if (email.isNotBlank() && password.isNotBlank()){
            try {
                repository.login(email,password)
                signInStatus.postValue(Resource.Success("","Succesfully signed-in!"))
            }catch (e : Exception){
                signInStatus.postValue(Resource.Error(e.message))
            }
        }else{
            signInStatus.postValue(Resource.Error("Please fill all the blanks!"))
        }
    }

}