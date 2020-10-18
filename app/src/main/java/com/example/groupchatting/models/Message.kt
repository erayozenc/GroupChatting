package com.example.groupchatting.models

data class Message(
    val user : User? = null,
    val message : String = "",
    val messageId : String = "",
    val timestamp : Long = 0L
)