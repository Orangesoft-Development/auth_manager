package co.orangesoft.authmanager.auth.user

import android.annotation.SuppressLint
import com.facebook.FacebookSdk.getApplicationContext
import android.provider.Settings.Secure
import by.orangesoft.auth.firebase.FirebaseProfile
import by.orangesoft.auth.firebase.FirebaseUserController
import by.orangesoft.auth.user.IBaseUserController
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import kotlin.jvm.Throws

@SuppressLint("HardwareIds")
class UnregisteredUserControllerImpl(firebaseInstance: FirebaseAuth): FirebaseUserController(firebaseInstance) {

    override var profile: FirebaseProfile =
        Profile(
            Secure.getString(
                getApplicationContext().contentResolver,
                Secure.ANDROID_ID
            ) ?: "unknown"
        )

    override var accessToken: String = ""

    @Throws(Throwable::class)
    override suspend fun saveChanges() {
        //do nothing
    }

    @Throws(Throwable::class)
    override suspend fun updateAvatar(file: File) {
        //do nothing
    }

    @Throws(Throwable::class)
    override suspend fun reload() {
        //do nothing
    }

}