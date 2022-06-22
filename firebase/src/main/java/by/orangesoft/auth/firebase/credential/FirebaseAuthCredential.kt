package by.orangesoft.auth.firebase.credential

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.BaseAuthCredential

/**
 * This is an open class of authorization credentials type using firebase
 *
 * @param provider Type of authorization credential
 * @see FirebaseProviders
 *
 */

@SuppressLint("ParcelCreator")
open class FirebaseAuthCredential(provider: FirebaseProviders) : BaseAuthCredential(provider.providerId) {

    /** Apple firebase credential */
    object Apple : FirebaseAuthCredential(FirebaseProviders.APPLE)

    /** Facebook firebase credential */
    object Facebook : FirebaseAuthCredential(FirebaseProviders.FACEBOOK)

    /** Google firebase credential
     * @param clientId - google server client id */
    data class Google(val clientId: String = "") : FirebaseAuthCredential(FirebaseProviders.GOOGLE)

    /** Phone firebase credential
     * @param phoneNumber - phone number for authorization
     * @param code - SMS code to confirm the phone number
     * @param verificationId - id of the current session verification
     * @param onCodeSentListener - callback that is called when an SMS code is sent to a phone number
     * */
    data class Phone(
        val phoneNumber: String = "",
        val code: String? = null,
        val verificationId: String? = null,
        val onCodeSentListener: ((verificationId: String) -> Unit)? = null
    ) : FirebaseAuthCredential(FirebaseProviders.PHONE)

}

/**
 * Enum of all types of providers for FirebaseAuthCredential
 *
 * @param providerId id of authorization credential
 * @see FirebaseAuthCredential
 *
 */

enum class FirebaseProviders(val providerId: String) {
    GOOGLE("google.com"),
    FACEBOOK("facebook.com"),
    APPLE("apple.com"),
    PHONE("phone")
}