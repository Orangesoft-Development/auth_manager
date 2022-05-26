package co.orangesoft.authmanager.auth

import android.content.Context
import by.orangesoft.auth.BaseAuthManager
import co.orangesoft.authmanager.api.provideAuthService
import co.orangesoft.authmanager.api.provideOkHttp
import co.orangesoft.authmanager.api.provideProfileService
import co.orangesoft.authmanager.firebase_auth.TokenManager
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.Interceptor

@InternalCoroutinesApi
class SimpleAuthManager(credManager: SimpleCredentialManager,
                        parentJob: Job? = null) : BaseAuthManager<SimpleUserController, SimpleCredentialManager>(credManager, parentJob) {

    companion object {

        const val BASE_URL = "http://api.github.com/"

        private val _user: MutableStateFlow<SimpleUserController> by lazy {
            MutableStateFlow(SimpleUserController())
        }

        fun getInstance(tokenServiceBaseUrl: String, appContext: Context, interceptors: List<Interceptor> = arrayListOf(), parentJob: Job? = null): SimpleAuthManager {
            val tokenManager = TokenManager(_user.asStateFlow(), tokenServiceBaseUrl, interceptors)
            val okHttp = provideOkHttp(interceptors, tokenManager)
            return SimpleAuthManager(SimpleCredentialManager(appContext, provideAuthService(BASE_URL, okHttp), provideProfileService(BASE_URL, okHttp), parentJob), parentJob)
        }
    }

    enum class UserStatus {
        UNREGISTERED,
        REGISTERED
    }

    override val user: MutableStateFlow<SimpleUserController> by lazy { _user }
    private val _status: MutableStateFlow<UserStatus> by lazy { MutableStateFlow(UserStatus.UNREGISTERED) }
    val userStatus: StateFlow<UserStatus> by lazy { _status.asStateFlow() }

    init {
        currentUser.onEach {
            _status.value = if (it.credentials.value.isEmpty()) UserStatus.UNREGISTERED else UserStatus.REGISTERED
        }.launchIn(this)
        _user.tryEmit(credManager.getCurrentUser())
    }

}