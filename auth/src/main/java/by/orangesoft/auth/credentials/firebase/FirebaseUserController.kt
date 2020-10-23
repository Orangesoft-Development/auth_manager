package by.orangesoft.auth.credentials.firebase

import android.util.Log
import by.orangesoft.auth.user.BaseUserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.coroutines.CoroutineContext

abstract class FirebaseUserController<PROFILE>(protected val firebaseInstance: FirebaseAuth): BaseUserController<PROFILE, BaseUserController.UserSettings> {

    abstract override val profile: PROFILE?

    override val settings by lazy {
        object : BaseUserController.UserSettings {}
    }

    val currentUser: FirebaseUser? = firebaseInstance.currentUser

    override suspend fun update() {
        currentUser?.let {
            firebaseInstance.updateCurrentUser(it)
        }
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
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
                    Log.e("FirebaseUserController", "Cannot get access token")
                }
            }
        }

        return token
    }
}
