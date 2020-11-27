package co.orangesoft.authmanager.firebase_auth

import by.orangesoft.auth.credentials.firebase.FirebaseAuthManager
import by.orangesoft.auth.credentials.firebase.FirebaseCredentialsManager
import co.orangesoft.authmanager.api.provideAuthService
import co.orangesoft.authmanager.api.provideOkHttp
import co.orangesoft.authmanager.api.provideProfileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AuthManager(credManager: FirebaseCredentialsManager) : FirebaseAuthManager(credManager), CoroutineScope {

    enum class UserStatus {
        REGISTERED,
        UNREGISTERED
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    val userStatus: MutableStateFlow<UserStatus> by lazy {
        MutableStateFlow(UserStatus.UNREGISTERED)
    }

    init {
        launch {
            getCredentials().collect { creds ->
                if (creds.isEmpty()) {
                    userStatus.value = UserStatus.UNREGISTERED
                } else {
                    userStatus.value = UserStatus.REGISTERED
                }
            }
        }
    }

    companion object {

        const val BASE_URL = "http://github.com"

        fun getInstance(): AuthManager {
            return AuthManager(
                CredentialManager(provideAuthService(BASE_URL, provideOkHttp()),
                    provideProfileService(BASE_URL, provideOkHttp())
                )
            )
        }
    }
}