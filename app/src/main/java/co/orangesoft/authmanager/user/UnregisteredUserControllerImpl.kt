package co.orangesoft.authmanager.user

import com.facebook.FacebookSdk.getApplicationContext
import android.provider.Settings.Secure
import by.orangesoft.auth.user.BaseUserController
import java.io.File

class UnregisteredUserControllerImpl: UserController {

    override val profile: Profile by lazy {
        Profile(Secure.getString(getApplicationContext().contentResolver, Secure.ANDROID_ID) ?: "unknown")
    }

    override val settings: Settings by lazy { Settings() }

    override suspend fun update() {}
    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {}

    override suspend fun refresh() {}

    override suspend fun getAccessToken(listener: suspend (String) -> Unit) {
        listener.invoke("")
    }
}