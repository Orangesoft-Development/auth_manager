package by.orangesoft.auth.credentials.firebase

data class FirebaseCredential(val uid: String,
                              val providerId: String,
                              var displayName: String = "",
                              var photoUrl: String = "",
                              var email: String = "",
                              var phoneNumber: String = "")