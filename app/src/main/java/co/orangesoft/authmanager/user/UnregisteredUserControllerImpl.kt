package co.orangesoft.authmanager.user

import com.facebook.FacebookSdk.getApplicationContext
import android.provider.Settings.Secure
import java.io.File

class UnregisteredUserControllerImpl: UserController {

    private var _accessToken: String = ""
    private var _refreshToken: String = ""

    override val profile: Profile by lazy {
        Profile(Secure.getString(getApplicationContext().contentResolver, Secure.ANDROID_ID) ?: "unknown")
    }

    override val settings: Settings by lazy { Settings() }

    override fun update() {}
    override fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {}

    override fun refresh() {}

    override fun getAccessToken(): String  = _accessToken
    override fun updateAccessToken(token: String) { _accessToken = token }

    override fun getRefreshToken(): String = _refreshToken
    override fun updateRefreshToken(token: String) { _refreshToken = token }

}