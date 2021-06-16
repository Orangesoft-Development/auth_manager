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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException
import kotlin.coroutines.CoroutineContext

abstract class BaseFirebaseCredentialController(override val authCredential: FirebaseAuthCredential): IBaseCredentialController, CoroutineScope {

    protected val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    protected lateinit var activityCallback: Task<AuthResult>
    protected fun isActivityCallbackInitialised() = ::activityCallback.isInitialized

    protected var flow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun addCredential() : Flow<CredentialResult> {
        getCredential()
        return flow.asSharedFlow()
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

    protected fun emitAuthTask(credential: AuthCredential) = getAuthTask(credential)?.let { getCredential() }

    protected open fun getCredential() {
        authInstance.currentUser?.let { user ->
            user.providerData.firstOrNull { it.providerId == authCredential.providerId }?.let {
                user.getIdToken(true)
                    .addOnSuccessListener { flow.tryEmit(CredentialResult(authCredential.providerId)) }
                    .addOnFailureListener {
                        authInstance.signOut()
                        onError("Error add credential ${authCredential.providerId}", it)
                    }
                return
            }
        }

        if (::activityCallback.isInitialized)
            activityCallback
                .addOnSuccessListener { result ->
                    result.user?.getIdToken(true)
                        ?.addOnSuccessListener { flow.tryEmit(CredentialResult(authCredential.providerId)) }
                        ?.addOnFailureListener {
                            authInstance.signOut()
                            onError("Error add credential ${authCredential.providerId}", it)
                        } ?: onError("Error add credential ${authCredential.providerId}", NullPointerException("Firebase user is null"))
                }
                .addOnFailureListener { onError("Error add credential ${authCredential.providerId}", it) }
    }

    protected open fun updateCurrentCredential(user: FirebaseUser, authCredential: AuthCredential) {}

    private fun getAuthTask(credential: AuthCredential): Task<AuthResult>? =
        authInstance.currentUser?.let { currentUser ->
            if (!currentUser.isAnonymous) {
                currentUser.providerData.firstOrNull { it.providerId == authCredential.providerId }?.let {
                    updateCurrentCredential(currentUser, credential)
                    return null
                } ?: currentUser.linkWithCredential(credential)
            } else null
        } ?: authInstance.signInWithCredential(credential)

}
