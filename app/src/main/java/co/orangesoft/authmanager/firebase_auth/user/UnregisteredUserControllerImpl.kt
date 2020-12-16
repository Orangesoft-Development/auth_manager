package co.orangesoft.authmanager.firebase_auth.user

import android.annotation.SuppressLint
import com.facebook.FacebookSdk.getApplicationContext
import android.provider.Settings.Secure
import by.orangesoft.auth.firebase.FirebaseProfile
import com.google.firebase.auth.FirebaseAuth
import java.io.File

@SuppressLint("HardwareIds")
class UnregisteredUserControllerImpl(firebaseInstance: FirebaseAuth): by.orangesoft.auth.firebase.FirebaseUserController(firebaseInstance) {

    override var profile: FirebaseProfile =
        Profile(
            Secure.getString(
                getApplicationContext().contentResolver,
                Secure.ANDROID_ID
            ) ?: "unknown"
        )

    override var accessToken: String = ""

    override suspend fun saveChanges(onError: ((Throwable) -> Unit)?) {
        //do nothing
    }

    override suspend fun updateAvatar(file: File, onError: ((Throwable) -> Unit)?) {
        //do nothing
    }

    override suspend fun reload(onError: ((Throwable) -> Unit)?) {
        //do nothing
    }

}