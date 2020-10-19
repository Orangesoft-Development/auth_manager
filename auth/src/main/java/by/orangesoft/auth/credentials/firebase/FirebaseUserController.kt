package by.orangesoft.auth.credentials.firebase

import by.orangesoft.auth.user.BaseUserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File

open class FirebaseUserController(protected val firebaseInstance: FirebaseAuth): BaseUserController<FirebaseUser, BaseUserController.UserSettings> {

    override val profile: FirebaseUser
        get() = firebaseInstance.currentUser!!

    override val settings: BaseUserController.UserSettings by lazy {
        object : BaseUserController.UserSettings {}
    }

    override suspend fun update() {
        firebaseInstance.updateCurrentUser(profile)
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
    }

    override suspend fun refresh() {
        profile.reload()
    }

    override suspend fun getAccessToken(listener: suspend (String) -> Unit) {
        listener.invoke(profile.getIdToken(true).result?.token ?: "")
    }
}
