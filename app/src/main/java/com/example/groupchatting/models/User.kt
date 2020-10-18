package com.example.groupchatting.models

data class User(
    val uid : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val email : String = "",
    val profilePhoto : String = "",
    val oneSignalId : String = "",
    val groupIds : List<String>? = null
)