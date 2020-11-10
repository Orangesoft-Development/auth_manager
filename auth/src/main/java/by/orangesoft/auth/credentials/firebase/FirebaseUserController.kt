package by.orangesoft.auth.credentials.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.user.BaseUserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.runBlocking
import java.io.File

abstract class FirebaseUserController<T>(protected val firebaseInstance: FirebaseAuth)
    : BaseUserController<T> {

    abstract override var profile: T?

    override val credentials: LiveData<Set<FirebaseCredential>> by lazy {
        MutableLiveData<Set<FirebaseCredential>>().apply { postValue(getCredentials()) }
    }

    val currentUser: FirebaseUser? = firebaseInstance.currentUser
    private val TAG = "FirebaseUserController";

    override suspend fun update() {
        currentUser?.let {
            firebaseInstance.updateCurrentUser(it)
        }
    }

    protected open fun getCredentials(): Set<FirebaseCredential> = firebaseInstance.currentUser?.providerData?.mapNotNull {
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
        (credentials as MutableLiveData).postValue(getCredentials())
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        //do nothing
    }

    override suspend fun refresh() {
        currentUser?.reload()
    }

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

    override fun updateAccount(function: (UserProfileChangeRequest.Builder) -> Unit) {
        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder().also {
                function.invoke(it)
            }.build()).addOnSuccessListener {
                firebaseInstance.updateCurrentUser(this)
            }.addOnFailureListener { Log.e(TAG, "Unable update firebase profile", it) }
        }
    }
}
