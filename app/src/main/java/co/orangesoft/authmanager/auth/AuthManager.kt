package co.orangesoft.authmanager.auth

import by.orangesoft.auth.firebase.FirebaseAuthManager
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.provideAuthService
import co.orangesoft.authmanager.api.provideOkHttp
import co.orangesoft.authmanager.api.provideProfileService
import co.orangesoft.authmanager.auth.user.UnregisteredUserControllerImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.Interceptor
import kotlin.coroutines.CoroutineContext

class AuthManager(credManager:FirebaseCredentialsManager,
                  parentJob: Job? = null) : FirebaseAuthManager(credManager), CoroutineScope {

    companion object {

        const val BASE_URL = "http://github.com"

        private val _user: MutableStateFlow<FirebaseUserController> by lazy {
            MutableStateFlow(UnregisteredUserControllerImpl(FirebaseAuth.getInstance()))
        }

        fun getInstance(tokenServiceBaseUrl: String, interceptors: List<Interceptor> = arrayListOf()): AuthManager {
            val tokenManager = TokenManager(_user.asStateFlow(), tokenServiceBaseUrl, interceptors)
            val okHttp = provideOkHttp(interceptors, tokenManager)
            return AuthManager(CredentialManager(provideAuthService(BASE_URL, okHttp), provideProfileService(BASE_URL, okHttp)))
        }
    }

    enum class UserStatus {
        REGISTERED,
        UNREGISTERED
    }

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob(parentJob) }

    private val _status: MutableStateFlow<UserStatus> by lazy {
        MutableStateFlow(UserStatus.UNREGISTERED)
    }

    val userStatus: StateFlow<UserStatus> by lazy {
        _status.asStateFlow()
    }

    override val user: MutableStateFlow<FirebaseUserController>
        get() = _user

    init {
        currentUser
            .onEach { user ->
                if (user.credentials.value.isEmpty()) {
                    _status.value = UserStatus.UNREGISTERED
                } else {
                    _status.value = UserStatus.REGISTERED
                }
            }.launchIn(this)
    }

    override val onAuthSuccessListener: (FirebaseUserController) -> Unit by lazy { {
        val newUser = if(it.credentials.value.isEmpty())
            UnregisteredUserControllerImpl(FirebaseAuth.getInstance())
        else
            it

        super.onAuthSuccessListener.invoke(newUser)
    } }
}