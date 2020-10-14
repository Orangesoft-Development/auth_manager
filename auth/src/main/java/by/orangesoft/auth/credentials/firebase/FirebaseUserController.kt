package by.orangesoft.auth.credentials.firebase

import by.orangesoft.auth.user.UserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File

open class FirebaseUserController(protected val firebaseInstance: FirebaseAuth): UserController<FirebaseUser, UserController.UserSettings> {

    override val profile: FirebaseUser
        get() = firebaseInstance.currentUser!!

    override val settings: UserController.UserSettings by lazy {
        object : UserController.UserSettings {}
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
