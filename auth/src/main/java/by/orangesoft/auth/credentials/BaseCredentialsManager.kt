package by.orangesoft.auth.credentials

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws

abstract class BaseCredentialsManager<T: BaseUserController<*>> (parentJob: Job? = null): CoroutineScope, IBaseCredentialsManager<T> {

    companion object {
        const val TAG = "CredentialsController"
    }

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + Job(parentJob) }

    private val userSharedFlow = MutableSharedFlow<T>(1, 1)

    abstract fun getCurrentUser(): T

    @Throws(Exception::class)
    protected abstract suspend fun onLogged(credentialResult: CredentialResult): T

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialAdded(credentialResult: CredentialResult, user: T)

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialRemoved(credential: IBaseCredential, user: T)

    abstract suspend fun logout(user: T): Flow<T>

    abstract suspend fun deleteUser(user: T): Flow<T>

    protected abstract fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder

    override fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?): Flow<T> =
        userSharedFlow.asSharedFlow().onStart { user?.credentials?.value?.firstOrNull { it.providerId == credential.providerId }
            ?.let { userSharedFlow.tryEmit(user) }
            ?: addBuilderCredential(activity, credential, user, currentCoroutineContext())
        }

    override fun removeCredential(credential: IBaseCredential, user: T): Flow<T> {
        return userSharedFlow.asSharedFlow().onStart {
            if (!user.credentials.value.let { creds -> creds.firstOrNull { it.providerId == credential.providerId } != null && creds.size > 1 }) {
                throw NoSuchElementException("Cannot remove method $credential")
            } else removeBuilderCredential(credential, currentCoroutineContext())
        }
    }

    protected fun getUpdatedUserFlow(): Flow<T> {
        userSharedFlow.tryEmit(getCurrentUser())
        return userSharedFlow.asSharedFlow()
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
                user?.let { clearCredInfo(credential) } ?: signOut()
                coroutineContext.job.cancel("Error add credential ${credential.providerId}", it)
            }
            .onCompletion {
                user?.let { clearCredInfo(credential) } ?: signOut()
                it?.let { coroutineContext.cancel(CancellationException(it.message, it.cause)) }
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
                it?.let { coroutineContext.cancel(CancellationException(it.message, it.cause)) }
            }
            .launchIn(CoroutineScope(coroutineContext + this.coroutineContext.job))
    }

}
