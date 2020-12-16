package by.orangesoft.auth.firebase.credential

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.AuthCredential

@SuppressLint("ParcelCreator")
open class Firebase(providerId: String): AuthCredential(providerId) {
    object Apple : Firebase("apple.com")
    object Facebook : Firebase("facebook.com")
    data class Google(val clientId: String) : Firebase("google.com")
}