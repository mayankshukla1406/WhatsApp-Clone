package com.example.whatsapp_clone

data class User(
    val profileUid: String,
    val profileName: String,
    val profileEmail: String,
    val profileStatus: String,
    val profilePicture: String,
    val chatRoomId : String
)
