package co.orangesoft.authmanager.user

import android.annotation.SuppressLint
import com.facebook.FacebookSdk.getApplicationContext
import android.provider.Settings.Secure
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import by.orangesoft.auth.user.BaseUserController
import com.google.firebase.auth.FirebaseAuth
import java.io.File


@SuppressLint("HardwareIds")
class UnregisteredUserControllerImpl(firebaseInstance: FirebaseAuth): FirebaseUserController<Profile>(firebaseInstance) {

    override val profile: Profile by lazy {
        Profile(Secure.getString(getApplicationContext().contentResolver, Secure.ANDROID_ID) ?: "unknown")
    }

    override val settings: Settings by lazy { Settings() }

    override suspend fun update() {
        //do nothing
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        //do nothing
    }

    override suspend fun refresh() {
        //do nothing
    }

    override suspend fun getAccessToken(): String = ""
}