package com.example.chatequipetunisie.models

data class User(
    var uuid: String = "",        // Firebase user ID (uid)
    var email: String = "",       // User email
    var fullName: String = "",    // User's full name
    var image: String? = null     // Optional: URL or base64 image string
) {
    // Default constructor needed for Firebase deserialization
    constructor() : this("", "", "", "")
}
