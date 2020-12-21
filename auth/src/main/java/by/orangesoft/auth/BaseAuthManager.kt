package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.user.IBaseUserController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

@InternalCoroutinesApi
abstract class BaseAuthManager<T: IBaseUserController<*>, C: BaseCredentialsManager<T>>(protected val credentialsManager: C, parentJob: Job? = null): IAuthManager<T>, CoroutineScope {

    protected abstract val user: MutableStateFlow<T>

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob(parentJob) }

    override val currentUser: StateFlow<T> by lazy { user.asStateFlow() }

    override fun login(activity: FragmentActivity, credential: AuthCredential): Job =
        launch {
            credentialsManager.addCredential(activity, credential, null)
                    .collectLatest { user.value = it }
        }

    override fun addCredential(activity: FragmentActivity, credential: AuthCredential): Job =
        launch {
            credentialsManager.addCredential(activity, credential, currentUser.value)
                    .collectLatest { user.value = it }
        }


    override fun removeCredential(credential: IBaseCredential): Job =
        launch {
            credentialsManager.removeCredential(credential, currentUser.value)
                    .collectLatest { user.value = it }
        }

}
