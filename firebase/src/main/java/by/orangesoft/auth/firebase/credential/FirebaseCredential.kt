package by.orangesoft.auth.firebase.credential

import by.orangesoft.auth.credentials.IBaseCredential

class FirebaseCredential(val uid: String,
                         override val providerId: String,
                         var displayName: String = "",
                         var photoUrl: String = "",
                         var email: String = "",
                         var phoneNumber: String = "") : IBaseCredential