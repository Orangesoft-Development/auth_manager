package by.orangesoft.auth.credentials

import android.util.Log
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

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob(parentJob) }

    protected val userSharedFlow = MutableSharedFlow<T>(1, 1)

    @Throws(Exception::class)
    protected abstract suspend fun onLogged(credentialResult: CredentialResult): T

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialAdded(credentialResult: CredentialResult, user: T)

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialRemoved(credential: IBaseCredential, user: T)

    @Throws(Exception::class)
    open suspend fun logout(user: T) {}

    @Throws(Exception::class)
    open suspend fun deleteUser(user: T) {}

    protected abstract fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder

    override fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?): Flow<T> {

        if (user?.credentials?.value?.firstOrNull { it.providerId == credential.providerId } != null) {
            userSharedFlow.tryEmit(user)
        } else {
            val credController = getBuilder(credential).build(activity)

            credController.addCredential()
                .onEach {
                    Log.e("TAG","onEach thred:${Thread.currentThread()}")
                    if(user != null) {
                        onCredentialAdded(it, user)
                        userSharedFlow.tryEmit(user)
                    } else {
                        userSharedFlow.tryEmit(onLogged(it))
                    }
                }
                .launchIn(this)
        }

        return userSharedFlow.asSharedFlow()
    }


    override fun removeCredential(credential: IBaseCredential, user: T): Flow<T> {
        if (!user.credentials.value.let { creds -> creds.firstOrNull { it.providerId == credential.providerId } != null && creds.size > 1 }) {
            throw NoSuchElementException("Cannot remove method $credential")
        }

        getBuilder(credential).build().removeCredential().invokeOnCompletion {
            runBlocking {
                onCredentialRemoved(credential, user)
            }
            userSharedFlow.tryEmit(user)
        }

        return userSharedFlow.asSharedFlow()
    }
}
