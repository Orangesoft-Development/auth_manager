package by.orangesoft.auth.credentials.firebase

import by.orangesoft.auth.credentials.BaseCredential

class FirebaseCredential(val uid: String,
                         providerId: String,
                         var displayName: String = "",
                         var photoUrl: String = "",
                         var email: String = "",
                         var phoneNumber: String = "") : BaseCredential(providerId)