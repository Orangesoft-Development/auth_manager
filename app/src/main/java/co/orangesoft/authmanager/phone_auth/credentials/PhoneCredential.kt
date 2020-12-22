package co.orangesoft.authmanager.phone_auth.credentials

import by.orangesoft.auth.credentials.IBaseCredential

class PhoneCredential(
    val uid: String,
    override val providerId: String,
    var displayName: String = "",
    var photoUrl: String = "",
    var email: String = "",
    var phoneNumber: String = ""
) : IBaseCredential