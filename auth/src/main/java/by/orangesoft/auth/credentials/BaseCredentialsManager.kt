package by.orangesoft.auth.credentials

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.user.IBaseUserController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws

@InternalCoroutinesApi
abstract class BaseCredentialsManager<T: IBaseUserController<*>> (parentJob: Job? = null): CoroutineScope, IBaseCredentialsManager<T> {

    companion object {
        const val TAG = "CredentialsController"
    }

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob(parentJob) }

    @Throws(Exception::class)
    protected abstract suspend fun onLogged(credentialResult: CredentialResult): T

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialAdded(credentialResult: CredentialResult, user: T)

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialRemoved(credential: IBaseCredential, user: T)

    protected abstract fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder

    override fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?): Flow<T> =
        flow {

            if (user?.credentials?.value?.firstOrNull { it == credential } != null) {
                emit(user)
                return@flow
            }

            getBuilder(credential).build(activity).addCredential()
                    .collectLatest {
                        if(user != null) {
                            onCredentialAdded(it, user)
                            emit(user)
                        } else
                            emit(onLogged(it))
                    }

        }


    override fun removeCredential(credential: IBaseCredential, user: T): Flow<T> =
            flow {
                if(!user.credentials.value.let { creds -> creds.firstOrNull { it == credential } != null && creds.size > 1 }) {
                    throw NoSuchElementException("Cannot remove method $credential")
                }

                getBuilder(credential).build().removeCredential()
                        .collectLatest {
                            onCredentialRemoved(credential, user)
                            emit(user)
                        }
            }
}
