package by.orangesoft.auth.credentials

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws

abstract class BaseCredentialsManager<T: BaseUserController<*>> (parentJob: Job? = null): CoroutineScope, IBaseCredentialsManager<T> {

    companion object {
        const val TAG = "CredentialsController"
    }

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + Job(parentJob) }

    private var userSharedFlow = MutableSharedFlow<T>(0, 1, BufferOverflow.DROP_OLDEST)

    abstract fun getCurrentUser(): T

    @Throws(Exception::class)
    protected abstract suspend fun onLogged(credentialResult: CredentialResult): T

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialAdded(credentialResult: CredentialResult, user: T)

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialRemoved(credential: IBaseCredential, user: T)

    @Throws(Exception::class)
    protected abstract suspend fun onUserLogout(user: T): T

    @Throws(Exception::class)
    protected abstract suspend fun onUserDelete(user: T): T

    protected abstract fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder

    override fun logout(user: T): Flow<T> = userSharedFlow.asSharedFlow().onStart { emit(onUserLogout(user)) }

    override fun deleteUser(user: T): Flow<T> = userSharedFlow.asSharedFlow().onStart { emit(onUserDelete(user)) }

    override fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?): Flow<T> =
        userSharedFlow.asSharedFlow().onStart { addBuilderCredential(activity, credential, user, currentCoroutineContext()) }

    override fun removeCredential(credential: IBaseCredential, user: T): Flow<T> =
        userSharedFlow.asSharedFlow().onStart {
            if (!user.credentials.value.let { creds -> creds.firstOrNull { it.providerId == credential.providerId } != null && creds.size > 1 }) {
                throw NoSuchElementException("Cannot remove method $credential")
            } else removeBuilderCredential(credential, currentCoroutineContext())
        }

    protected fun getUpdatedUserFlow(): Flow<T> {
        return userSharedFlow.asSharedFlow().onStart { userSharedFlow.emit(getCurrentUser()) }
    }

    private fun addBuilderCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?, coroutineContext: CoroutineContext) {
        getBuilder(credential).build(activity).addCredential()
            .onEach { credResult ->
                user?.let {
                    getCurrentUser().let { user ->
                        onCredentialAdded(credResult, user)
                        userSharedFlow.tryEmit(user)
                    }
                } ?: userSharedFlow.tryEmit(onLogged(credResult))
            }
            .catch {
                user?.let { clearCredInfo(credential, true) } ?: signOut()
                coroutineContext.job.cancel("Error add credential ${credential.providerId}", it)
            }
            .onCompletion {
                it?.let {
                    user?.let { clearCredInfo(credential, true) } ?: signOut()
                    coroutineContext.cancel(CancellationException(it.message, it.cause))
                }
            }
            .launchIn(CoroutineScope(coroutineContext + this.coroutineContext.job))
    }

    private suspend fun removeBuilderCredential(credential: IBaseCredential, coroutineContext: CoroutineContext) {
        getBuilder(credential).build().removeCredential()
            .onEach {
                getCurrentUser().let {
                    onCredentialRemoved(credential, it)
                    userSharedFlow.tryEmit(it)
                }
            }.catch {
                coroutineContext.job.cancel("Error remove credential ${credential.providerId}", it)
            }
            .onCompletion {
                it?.let {
                    coroutineContext.cancel(CancellationException(it.message, it.cause))
                }
            }
            .launchIn(CoroutineScope(coroutineContext + this.coroutineContext.job))
    }

}
