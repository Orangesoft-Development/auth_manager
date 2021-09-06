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
        credentialsManager.addCredential(activity, credential, null).takeSingleUserFlow()

    override fun login(activity: FragmentActivity, credential: BaseAuthCredential): Job =
        loginFlow(activity, credential).launchIn(this)

    override fun addCredentialFlow(activity: FragmentActivity, credential: BaseAuthCredential): Flow<T> =
        credentialsManager.addCredential(activity, credential, currentUser.value).takeSingleUserFlow()

    override fun addCredential(activity: FragmentActivity, credential: BaseAuthCredential): Job =
        addCredentialFlow(activity, credential).launchIn(this)

    override fun removeCredentialFlow(credential: IBaseCredential): Flow<T> =
        credentialsManager.removeCredential(credential, currentUser.value).takeSingleUserFlow()

    override fun removeCredential(credential: IBaseCredential): Job =
        removeCredentialFlow(credential).launchIn(this)

    override fun logoutFlow() = credentialsManager.logout(currentUser.value).takeSingleUserFlow()

    override fun logout() = logoutFlow().launchIn(this)

    override fun deleteUserFlow() = credentialsManager.deleteUser(currentUser.value).takeSingleUserFlow()

    override fun deleteUser() = deleteUserFlow().launchIn(this)

    private fun Flow<T>.takeSingleUserFlow() = onEach { user.value = it }.take(1)

}
