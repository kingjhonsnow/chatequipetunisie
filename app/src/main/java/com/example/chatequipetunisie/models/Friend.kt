package com.example.chatequipetunisie.models

import android.media.Image
import java.sql.Timestamp

data class Friend(
    val name: String,
    val lastM: String,
    val image: String?,  // Make it nullable if the image is optional
    val timestamp: Long
)
