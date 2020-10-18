package com.example.groupchatting.ui.chatting

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupchatting.models.Group
import com.example.groupchatting.models.Message
import com.example.groupchatting.models.User
import com.example.groupchatting.repository.MainRepository
import com.example.groupchatting.util.Resource
import com.google.firebase.firestore.Query
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class ChattingViewModel @ViewModelInject constructor(
        private val repository: MainRepository
) : ViewModel() {


    val createGroupStatus = MutableLiveData<Resource<Group>>()
    val joinGroupStatus = MutableLiveData<Resource<Group>>()
    val showGroupsStatus = MutableLiveData<Resource<List<Group>>>()
    val newMessageStatus = MutableLiveData<Resource<Any>>()
    val showMessagesStatus = MutableLiveData<Resource<List<Message>>>()
    val allUsersInGroupStatus = MutableLiveData<Resource<List<User>>>()
    val currentUser = MutableLiveData<User>()

    fun signOut() = repository.signOut()

    fun changePassword(password: String) = viewModelScope.launch {
        repository.updatePassword(password)
    }

    fun changeProfilePhoto(uri : Uri) = viewModelScope.launch {
        repository.updateUserProfileImage(uri)
    }

    fun createGroup(name: String,password: String,uri: Uri) = viewModelScope.launch(IO) {
        createGroupStatus.postValue(Resource.Loading())
        try {
            val group = repository.saveGroup(name,password,uri)
            createGroupStatus.postValue(Resource.Success(group,"Succesfull"))
            repository.saveUserToGroup(group.groupId)
        }catch (e : Exception){
            createGroupStatus.postValue(Resource.Error("Group could not be created."))
        }
    }

    fun joinGroup(name : String,password: String) = viewModelScope.launch {
        joinGroupStatus.postValue(Resource.Loading())
        try {
            lateinit var group : Group
            repository.getGroups().addSnapshotListener { value, error ->
                error?.let { throw it }
                value?.let {
                    for(document in it){
                        if (document.data.getValue("name") == name && document.data.getValue("password") == password){
                            group = document.toObject(Group::class.java)
                            println(group.name)
                            joinGroupStatus.postValue(Resource.Success(group))
                            break
                        }
                    }
                }
            }
            //repository.saveUserToGroup(group.groupId)
        }catch (e : Exception){
            joinGroupStatus.postValue(Resource.Error(e.message))
            println(e.localizedMessage)
        }
    }

    fun saveJoinedUserToGroup(groupId: String) = viewModelScope.launch {
        repository.saveUserToGroup(groupId)
    }

    fun showGroups() = viewModelScope.launch(IO) {
        showGroupsStatus.postValue(Resource.Loading())
        val groupIdList = repository.getCurrentUserGroupIds().toMutableList()
        try {
            repository.getGroups().addSnapshotListener { value, error ->
                error?.let { throw it }
                val groupList : MutableList<Group> = mutableListOf()
                value?.let {
                    for (document in it){
                        groupIdList?.let {
                            for (id in it){
                                if(document.data.getValue("groupId") == id){
                                    val group = document.toObject(Group::class.java)
                                    groupList.add(group)
                                }
                            }
                        }
                    }
                    showGroupsStatus.postValue(Resource.Success(groupList))
                }
            }
        }catch (e : Exception){
            showGroupsStatus.postValue(Resource.Error(e.message))
        }
    }

    fun textMessage(message : String,groupId : String) = viewModelScope.launch {
        try {
            repository.textMessage(message,groupId)
            newMessageStatus.postValue(Resource.Success("",""))
        }catch (e : Exception){
            newMessageStatus.postValue(Resource.Error(e.message))
        }
    }

    fun getUsersInGroup(groupId: String) = viewModelScope.launch {
        try {
            repository.getUsersInGroup(groupId).addSnapshotListener { value, error ->
                error?.let { throw it }
                val users : MutableList<User> = mutableListOf()
                value?.let {
                    for (document in it){
                        val user = document.toObject(User::class.java)
                        users.add(user)
                    }
                }
                allUsersInGroupStatus.postValue(Resource.Success(users))
            }
        }catch (e : Exception){
            allUsersInGroupStatus.postValue(Resource.Error(e.message))
        }
    }

    fun sendUsersNotification(userList : List<User>){
        val uid = repository.getUid()
        for (user in userList){
            if(user.uid == uid){
                continue
            }
            try {
                OneSignal.postNotification(JSONObject("{'contents': {'en':'"+ "New Message" +"'}, 'include_player_ids': ['" + user.oneSignalId + "']}"), null)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    fun showMessages(groupId: String) = viewModelScope.launch(IO) {
        showMessagesStatus.postValue(Resource.Loading())
        try {
            repository.getMessages(groupId)
                .orderBy("timestamp",Query.Direction.ASCENDING)
                .addSnapshotListener { value, error ->
                    error?.let { throw it }
                    val messageList : MutableList<Message> = mutableListOf()
                    value?.let {
                        for (document in it){
                            val chatMessage = document.toObject(Message::class.java)
                            messageList.add(chatMessage)
                        }
                    }
                    showMessagesStatus.postValue(Resource.Success(messageList,""))
                }
        }catch (e : Exception){
            showMessagesStatus.postValue(Resource.Error(e.message))
        }
    }

    fun getCurrentUserUid() : String {
        return repository.getUid()!!
    }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getUser()
        currentUser.postValue(user)
    }

}