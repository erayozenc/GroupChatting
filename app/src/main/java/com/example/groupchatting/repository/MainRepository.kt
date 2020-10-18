package com.example.groupchatting.repository

import android.net.Uri
import com.example.groupchatting.models.Group
import com.example.groupchatting.models.Message
import com.example.groupchatting.models.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.*

class MainRepository(
    private val auth : FirebaseAuth,
    private val firestoreRef : FirebaseFirestore,
    private val storageRef : StorageReference
) {

    suspend fun signup(email : String, password : String): AuthResult
    = auth.createUserWithEmailAndPassword(email,password).await()

    fun getUid() = auth.uid

    suspend fun login(email: String,password: String): AuthResult
    = auth.signInWithEmailAndPassword(email,password).await()

    fun signOut() = auth.signOut()

    suspend fun uploadProfilePhoto(uri : Uri){
        val photoRef = storageRef.child("ProfilePhotos/${auth.uid!!}")
        photoRef.putFile(uri).await()
    }

    suspend fun getProfilePhotoUrl() : String{
        val photoRef = storageRef.child("ProfilePhotos/${auth.uid!!}")
        return photoRef.downloadUrl.await().toString()
    }

    suspend fun saveUser(user : User){
        val userRef = firestoreRef.collection("Users").document(auth.uid!!)
        userRef.set(user).await()
    }

    suspend fun getUser() : User{
        val userRef = auth.uid?.let {
            firestoreRef.collection("Users").document(it)
        }
        val user = userRef?.get()?.await()?.toObject(User::class.java)
        return user!!
    }

    suspend fun updatePassword(password: String) = auth.currentUser!!.updatePassword(password).await()

    suspend fun updateUserProfileImage(photoUri : Uri) {
        val ref = storageRef.child("ProfilePhotos/${auth.uid}")
        ref.delete().addOnCompleteListener {
            ref.putFile(photoUri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val uploadedUrl = it.toString()
                    val currentUserRef = firestoreRef.collection("Users")
                        .document(getUid()!!)
                    currentUserRef.update("profilePhoto",uploadedUrl)

                }
            }
        }
    }

    suspend fun saveGroup(name : String,password : String,uri : Uri) : Group{
        val groupRef = firestoreRef.collection("Groups").document()
        val iconRef = storageRef.child("GroupIcons/${groupRef.id}")
        iconRef.putFile(uri).await()
        val iconUrl = iconRef.downloadUrl.await().toString()
        val group = Group(name,password,groupRef.id,iconUrl)
        groupRef.set(group).await()
        return group
    }

    suspend fun getCurrentUserGroupIds() : List<String> {
        val currentUserRef = auth.uid?.let{
            firestoreRef.collection("Users").document(it)
        }
        val currentUser = currentUserRef?.get()?.await()?.toObject(User::class.java)
        return currentUser?.groupIds ?: mutableListOf<String>()
    }

    fun getGroups() = firestoreRef.collection("Groups")

    suspend fun saveUserToGroup(groupId : String){
        val userInGroupRef = firestoreRef.collection("Groups")
                .document(groupId).collection("User List").document(auth.uid!!)

        val userRef = auth.uid?.let {
            firestoreRef.collection("Users").document(it)
        }
        val user = userRef?.get()?.await()?.toObject(User::class.java)

        userInGroupRef.set(user!!)
        userRef.update("groupIds",FieldValue.arrayUnion(groupId))
    }


    suspend fun textMessage(message : String,groupId: String){
        val newMessageRef = firestoreRef.collection("Groups").document(groupId).collection("Group Messages").document()
        val userRef = auth.uid?.let {
            firestoreRef.collection("Users").document(it)
        }
        val user = userRef?.get()?.await()?.toObject(User::class.java)
        val newMessage = Message(user!!,message,newMessageRef.id,System.currentTimeMillis())
        newMessageRef.set(newMessage)
    }

    fun getMessages(groupId: String) = firestoreRef.collection("Groups").document(groupId).collection("Group Messages")

    fun getUsersInGroup(groupId: String) = firestoreRef.collection("Groups").document(groupId).collection("User List")

}