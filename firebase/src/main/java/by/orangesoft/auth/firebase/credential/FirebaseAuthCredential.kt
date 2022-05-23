package by.orangesoft.auth.firebase.credential

import android.annotation.SuppressLint
import android.os.Parcelable
import by.orangesoft.auth.credentials.BaseAuthCredential

@SuppressLint("ParcelCreator")
open class FirebaseAuthCredential(providerId: String) : BaseAuthCredential(providerId) {
    object Apple : FirebaseAuthCredential(Providers.APPLE)
    object Facebook : FirebaseAuthCredential(Providers.FACEBOOK)
    data class Phone(
        val phoneNumber: String,
        val code: String? = null,
        val verificationId: String? = null,
        val onCodeSentListener: ((verificationId: String) -> Unit)? = null
    ) : FirebaseAuthCredential(Providers.PHONE)
    data class Google(val clientId: String) : FirebaseAuthCredential(Providers.GOOGLE)
}

object Providers {
    const val GOOGLE = "google.com"
    const val FACEBOOK = "facebook.com"
    const val APPLE = "apple.com"
    const val PHONE = "phone"
}