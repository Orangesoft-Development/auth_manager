package by.orangesoft.auth.credentials.firebase

open class FirebaseProfile(
    var uid: String,
    var providerId: String? = null,
    var displayName: String? = null,
    var phoneNumber: String? = null,
    var photoUrl: String? = null,
    var email: String? = null
)