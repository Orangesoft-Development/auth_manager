package by.orangesoft.auth.firebase.credential

import by.orangesoft.auth.credentials.CredentialResult
import com.google.firebase.auth.FirebaseAuth

data class FirebaseCredentialResult(val uid: String,
                                    override val providerId: String,
                                    override val token: String,
                                    var displayName: String = "",
                                    var photoUrl: String = "",
                                    var email: String = "",
                                    var phoneNumber: String = "") : CredentialResult(providerId, token) {

    override fun equals(other: Any?): Boolean =
        when(other) {
            is String           -> providerId == other
            else                -> super.equals(other)
        }

    override fun hashCode(): Int = providerId.hashCode()

    override fun toString(): String = "${javaClass.simpleName}($providerId)"
}


fun FirebaseAuth.getCredentials(): Set<FirebaseCredentialResult> =
        currentUser?.providerData?.mapNotNull {
            if (it.providerId != "firebase")
                FirebaseCredentialResult(
                        it.uid,
                        it.providerId,
                        it.displayName ?: "",
                        it.photoUrl?.path ?: "",
                        it.email ?: "",
                        it.phoneNumber ?: ""
                )
            else
                null
        }?.toSet() ?: HashSet()