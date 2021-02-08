package by.orangesoft.auth.firebase.credential

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.AuthCredential

@SuppressLint("ParcelCreator")
open class Firebase(providerId: String): AuthCredential(providerId) {
    object Apple : Firebase(Providers.APPLE)
    object Facebook : Firebase(Providers.FACEBOOK)
    data class Google(val clientId: String) : Firebase(Providers.GOOGLE)
}

object Providers {
    const val GOOGLE = "google.com"
    const val FACEBOOK = "facebook.com"
    const val APPLE = "apple.com"
}