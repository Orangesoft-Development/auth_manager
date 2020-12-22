package co.orangesoft.authmanager.phone_auth

import by.orangesoft.auth.AuthListener
import by.orangesoft.auth.BaseAuthManager
import co.orangesoft.authmanager.api.provideAuthService
import co.orangesoft.authmanager.api.provideOkHttp
import co.orangesoft.authmanager.api.provideProfileService
import co.orangesoft.authmanager.firebase_auth.TokenManager
import co.orangesoft.authmanager.phone_auth.credentials.PhoneCredentialsManager
import co.orangesoft.authmanager.phone_auth.user.PhoneUserController
import co.orangesoft.authmanager.phone_auth.user.UnregisteredPhoneUserController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import okhttp3.Interceptor
import kotlin.coroutines.CoroutineContext

class PhoneAuthManager(credManager: PhoneCredentialsManager, parentJob: Job? = null) : BaseAuthManager<PhoneUserController, PhoneCredentialsManager>(credManager), CoroutineScope {

    companion object {

        const val BASE_URL = "http://github.com"

        private val _user: MutableStateFlow<PhoneUserController> by lazy {
            MutableStateFlow(UnregisteredPhoneUserController())
        }

        fun getInstance(tokenServiceBaseUrl: String, interceptors: List<Interceptor> = arrayListOf()): PhoneAuthManager {
            val tokenManager = TokenManager(_user.asStateFlow(), tokenServiceBaseUrl, interceptors)
            val okHttp = provideOkHttp(interceptors, tokenManager)
            return PhoneAuthManager(PhoneCredentialsManager(provideAuthService(BASE_URL, okHttp), provideProfileService(BASE_URL, okHttp)))
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

    override val user: MutableStateFlow<PhoneUserController>
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

    override val onAuthSuccessListener: (PhoneUserController) -> Unit by lazy { {
        val newUser = if(it.credentials.value.isEmpty()) {
            UnregisteredPhoneUserController()
        } else {
            it
        }

        super.onAuthSuccessListener.invoke(newUser)
    } }

    override suspend fun logout(listener: AuthListener<PhoneUserController>?) {
        authListener = listener
        credentialsManager.logout(currentUser.value)
    }

    override suspend fun deleteUser(listener: AuthListener<PhoneUserController>?) {
        authListener = listener
        credentialsManager.deleteUser(currentUser.value)
    }
}