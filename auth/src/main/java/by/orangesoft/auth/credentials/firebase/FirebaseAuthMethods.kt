package by.orangesoft.auth.credentials.firebase

import android.annotation.SuppressLint
import by.orangesoft.auth.AuthMethod

@SuppressLint("ParcelCreator")
open class Firebase(providerId: String): AuthMethod(providerId) {
    object Apple : Firebase("apple.com")
    object Facebook : Firebase("facebook.com")
    data class Google(val clientId: String) : Firebase("google.com")
}