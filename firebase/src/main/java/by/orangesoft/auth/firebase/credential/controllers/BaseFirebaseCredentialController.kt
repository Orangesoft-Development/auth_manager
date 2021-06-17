package by.orangesoft.auth.firebase.credential.controllers

import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialController
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException
import kotlin.coroutines.CoroutineContext

abstract class BaseFirebaseCredentialController(override val authCredential: FirebaseAuthCredential): IBaseCredentialController, CoroutineScope {

    protected val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var credResultFlow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)
    protected var authTaskFlow: MutableSharedFlow<Task<AuthResult>?> = MutableSharedFlow(1, 1)

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
            val authTask = if (!currentUser.isAnonymous) currentUser.linkWithCredential(credential) else null
            authTask
        } ?: authInstance.signInWithCredential(credential))
    }


    protected fun getCredential() {
        authInstance.currentUser?.let { user ->
            user.providerData.firstOrNull { it.providerId == authCredential.providerId }?.let {
                credResultFlow.tryEmit(CredentialResult(authCredential.providerId))
            }
            return
        }

        authTaskFlow.onEach { task ->
            task?.addOnSuccessListener {
               credResultFlow.tryEmit(CredentialResult(authCredential.providerId))
            }?.addOnFailureListener {
                authInstance.signOut()
                onError("Error add credential ${authCredential.providerId}", it)
            } ?: onError("Error add credential ${authCredential.providerId}", NullPointerException("Auth task is null"))
        }.catch {
            onError("Error add credential ${authCredential.providerId}", it)
        }
    }
}
