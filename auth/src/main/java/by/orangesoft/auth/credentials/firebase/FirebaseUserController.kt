package by.orangesoft.auth.credentials.firebase

import android.util.Log
import by.orangesoft.auth.credentials.BaseCredential
import by.orangesoft.auth.user.BaseUserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import java.io.File

open class FirebaseUserController(protected val firebaseInstance: FirebaseAuth) : BaseUserController<FirebaseProfile> {

    val currentUser: FirebaseUser? = firebaseInstance.currentUser

    override var profile = currentUser?.let {
        FirebaseProfile(it.uid,
            it.providerId,
            it.displayName,
            it.phoneNumber,
            it.photoUrl.toString(),
            it.email)
    }

    override val credentials: MutableStateFlow<Set<BaseCredential>> by lazy {
        MutableStateFlow<Set<BaseCredential>>(getCredentialsList())
    }

    override suspend fun update() {
        currentUser?.let {
            firebaseInstance.updateCurrentUser(it)
        }
    }

    protected open fun getCredentialsList(): Set<FirebaseCredential> = firebaseInstance.currentUser?.providerData?.mapNotNull {
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

    fun updateCredentials() {
        credentials.value = getCredentialsList()
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        //do nothing
    }

    override fun updateAccount(profile: FirebaseProfile?) {
        //do nothing
    }

    override suspend fun refresh() {
        currentUser?.reload()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getAccessToken(): String {
        var token = ""
        runBlocking {
            firebaseInstance.currentUser?.getIdToken(true)?.addOnCompleteListener {
                if  (it.isSuccessful) {
                    token = it.result?.token ?: ""
                } else {
                    Log.e(TAG, "Cannot get access token")
                }
            }
        }

        return token
    }

    companion object {
        private const val TAG = "FirebaseUserController"
    }
}
