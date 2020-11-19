package co.orangesoft.authmanager.firebase_auth.user

import android.annotation.SuppressLint
import com.facebook.FacebookSdk.getApplicationContext
import android.provider.Settings.Secure
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.models.Profile
import com.google.firebase.auth.FirebaseAuth
import java.io.File


@SuppressLint("HardwareIds")
class UnregisteredUserControllerImpl(firebaseInstance: FirebaseAuth): FirebaseUserController<Profile>(firebaseInstance) {

    override var profile: Profile? =
        Profile(
            Secure.getString(
                getApplicationContext().contentResolver,
                Secure.ANDROID_ID
            ) ?: "unknown"
        )

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