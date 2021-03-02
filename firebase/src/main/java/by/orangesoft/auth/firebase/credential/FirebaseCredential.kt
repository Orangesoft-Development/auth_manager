package by.orangesoft.auth.firebase.credential

import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import com.google.firebase.auth.FirebaseAuth

data class FirebaseCredential(val uid: String,
                         override val providerId: String,
                         var displayName: String = "",
                         var photoUrl: String = "",
                         var email: String = "",
                         var phoneNumber: String = "") : IBaseCredential {

    override fun equals(other: Any?): Boolean =
            when(other) {
                is AuthCredential   -> providerId == other.providerId
                is IBaseCredential  -> providerId == other.providerId
                is String           -> providerId == other
                else                -> super.equals(other)
            }

    override fun hashCode(): Int = providerId.hashCode()

    override fun toString(): String = "${javaClass.simpleName}($providerId)"
}


fun FirebaseAuth.getCredentials(): Set<FirebaseCredential> =
        currentUser?.providerData?.mapNotNull {
            if (it.providerId != "firebase")
                FirebaseCredential(
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