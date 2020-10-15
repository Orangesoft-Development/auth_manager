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

    override fun update() {
        firebaseInstance.updateCurrentUser(profile)
    }

    override fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
    }

    override fun refresh() {
        profile.reload()
    }

    override fun getAccessToken(): String =
        profile.getIdToken(true).result?.token ?: ""
}
