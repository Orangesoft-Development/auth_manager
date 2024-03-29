package co.orangesoft.authmanager.firebase_auth

import android.accounts.AccountManager
import android.content.Context
import by.orangesoft.auth.firebase.FirebaseAuthManager
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.provideAuthService
import co.orangesoft.authmanager.api.provideOkHttp
import co.orangesoft.authmanager.api.provideProfileService
import co.orangesoft.authmanager.firebase_auth.user.UnregisteredUserControllerImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.Interceptor

@InternalCoroutinesApi
class AuthManager(credManager: FirebaseCredentialsManager,
                  parentJob: Job? = null) : FirebaseAuthManager(credManager, parentJob) {

    companion object {

        const val BASE_URL = "http://api.github.com/"

        private val _user: MutableStateFlow<FirebaseUserController> by lazy {
            MutableStateFlow(UnregisteredUserControllerImpl(FirebaseAuth.getInstance()))
        }

        fun getInstance(appContext: Context, tokenServiceBaseUrl: String, interceptors: List<Interceptor> = arrayListOf(), parentJob: Job? = null): AuthManager {
            val tokenManager = TokenManager(_user.asStateFlow(), tokenServiceBaseUrl, interceptors)
            val okHttp = provideOkHttp(interceptors, tokenManager)
            return AuthManager(CredentialManager(AccountManager.get(appContext), appContext.packageName, "", provideAuthService(BASE_URL, okHttp), provideProfileService(BASE_URL, okHttp), appContext, parentJob), parentJob)
        }
    }

    enum class UserStatus {
        UNREGISTERED,
        REGISTERED
    }

    override val user: MutableStateFlow<FirebaseUserController>
        get() = _user

    private val _status: MutableStateFlow<UserStatus> by lazy { MutableStateFlow(UserStatus.UNREGISTERED) }
    val userStatus: StateFlow<UserStatus> by lazy { _status.asStateFlow() }

    init {
        currentUser.onEach {
            _status.value = if(it.credentials.value.isEmpty()) UserStatus.UNREGISTERED else UserStatus.REGISTERED
        }.launchIn(this)
        _user.tryEmit(credManager.getCurrentUser())
    }
}