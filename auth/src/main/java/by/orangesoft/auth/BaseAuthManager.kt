package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

abstract class BaseAuthManager<T: BaseUserController<*>, C: BaseCredentialsManager<T>>(protected val credentialsManager: C, parentJob: Job? = null): IAuthManager<T>, CoroutineScope {

    protected abstract val user: MutableStateFlow<T>

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob(parentJob) }

    override val currentUser: StateFlow<T> by lazy { user.asStateFlow() }

    override fun loginFlow(activity: FragmentActivity, credential: BaseAuthCredential): Flow<T> =
        credentialsManager.addCredential(activity, credential, null)
            .onEach { user.value = it }

    override fun login(activity: FragmentActivity, credential: BaseAuthCredential): Job =
        loginFlow(activity, credential).launchIn(this)

    override fun addCredentialFlow(activity: FragmentActivity, credential: BaseAuthCredential): Flow<T> =
        credentialsManager.addCredential(activity, credential, currentUser.value)
            .onEach { user.value = it }

    override fun addCredential(activity: FragmentActivity, credential: BaseAuthCredential): Job =
        addCredentialFlow(activity, credential).launchIn(this)

    override fun removeCredentialFlow(credential: IBaseCredential): Flow<T> =
        credentialsManager.removeCredential(credential, currentUser.value)
            .onEach { user.value = it }

    override fun removeCredential(credential: IBaseCredential): Job =
        removeCredentialFlow(credential).launchIn(this)

    //todo refactoring on same logic
    override suspend fun logout() {
        credentialsManager.logout(currentUser.value)
            .onEach { user.value = it }
            .launchIn(this)
    }

    override suspend fun deleteUser() {
        credentialsManager.deleteUser(currentUser.value)
            .onEach { user.value = it }
            .launchIn(this)
    }

}
