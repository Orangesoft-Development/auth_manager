package by.orangesoft.auth.credentials.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.credentials.BaseCredential
import by.orangesoft.auth.user.BaseUserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.runBlocking
import java.io.File

open class FirebaseUserController<T>(protected val firebaseInstance: FirebaseAuth) : BaseUserController<T> {

    override var profile: T? = null

    override val credentials: LiveData<Set<BaseCredential>> by lazy {
        MutableLiveData<Set<BaseCredential>>().apply { postValue(getCredentialsList()) }
    }

    val currentUser: FirebaseUser? = firebaseInstance.currentUser
    private val TAG = "FirebaseUserController"

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
        (credentials as MutableLiveData).postValue(getCredentialsList())
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
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

    override fun updateAccount(profile: T?) {}
}
