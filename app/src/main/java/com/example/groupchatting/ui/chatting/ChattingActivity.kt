package com.example.groupchatting.ui.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.groupchatting.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)
    }
}