package by.orangesoft.auth.firebase.credential.controllers

import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialController
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

abstract class BaseFirebaseCredentialController(override val authCredential: FirebaseAuthCredential): IBaseCredentialController, CoroutineScope {

    protected val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var credResultFlow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)
    protected var authTaskFlow: MutableSharedFlow<Task<AuthResult>> = MutableSharedFlow(1, 1)

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun addCredential() : Flow<CredentialResult> {
        getCredential()
        return credResultFlow.asSharedFlow()
    }

    override fun removeCredential(): Job {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == authCredential.providerId
        }?.let { provider ->
            return launch {
                authInstance.currentUser?.unlink(provider.providerId)?.await()
            }
        }

        return coroutineContext.job
    }

    protected open fun onError(error: CancellationException) {
        coroutineContext.cancel(error)
    }

    protected open fun onError(message: String, cause: Throwable) {
        coroutineContext.job.cancel(message, cause)
    }

    protected fun emitAuthTask(credential: AuthCredential) {
        authTaskFlow.tryEmit(authInstance.currentUser?.let { currentUser ->
            val authTask = if (!currentUser.isAnonymous) currentUser.linkWithCredential(credential) else TODO("continue with task")
            authTask
        } ?: authInstance.signInWithCredential(credential))
    }


    protected fun getCredential() {
        launch {
            authInstance.currentUser?.let { user ->
                user.providerData.firstOrNull { it.providerId == authCredential.providerId }?.let {
                    credResultFlow.tryEmit(CredentialResult(authCredential.providerId, getToken(user)))
                }
                return@launch
            }

            authTaskFlow.onEach { task ->
                credResultFlow.tryEmit(CredentialResult(authCredential.providerId, getToken(task.await().user
                        ?: throw KotlinNullPointerException("FirebaseUser cannot be null"))))
            }.catch {
                onError("Error add credential ${authCredential.providerId}", it)
                authInstance.signOut()
            }.launchIn(this)
        }
    }

    private suspend fun getToken(user: FirebaseUser): String {
        return user.getIdToken(true).await().token ?: throw KotlinNullPointerException("Token must not be null")
    }
}
