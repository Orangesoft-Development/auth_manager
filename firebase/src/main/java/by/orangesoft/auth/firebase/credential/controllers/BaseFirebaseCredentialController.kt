package by.orangesoft.auth.firebase.credential.controllers

import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.IBaseCredentialController
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.getCredentials
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

abstract class BaseFirebaseCredentialController(override val credential: Firebase): IBaseCredentialController, CoroutineScope {

    protected val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    protected lateinit var activityCallback: Task<AuthResult>

    private var flow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun addCredential() : Flow<CredentialResult> {
        getCredential()
        return flow.asSharedFlow()
    }

    override fun removeCredential(): Job {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == credential.providerId
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

    protected fun getAuthTask(credential: AuthCredential): Task<AuthResult> =
        authInstance.currentUser?.let { currentUser ->
            if(!currentUser.isAnonymous)
                currentUser.linkWithCredential(credential)
            else null
        } ?: authInstance.signInWithCredential(credential)


    protected fun getCredential() {
        authInstance.currentUser?.let { user ->
            user.providerData.firstOrNull { it.providerId == credential.providerId }?.let {
                user.getIdToken(true)
                    .addOnSuccessListener { flow.tryEmit(CredentialResult(credential, it.token ?: "")) }
                    .addOnFailureListener {
                        authInstance.signOut()
                        onError("Error add credential ${credential.providerId}", it)
                    }
                return
            }
        }

        if(::activityCallback.isInitialized)
            activityCallback
                .addOnSuccessListener { result ->
                    result.user?.getIdToken(true)
                        ?.addOnSuccessListener { flow.tryEmit(CredentialResult(credential, it.token ?: "")) }
                        ?.addOnFailureListener {
                            authInstance.signOut()
                            onError("Error add credential ${credential.providerId}", it)
                        } ?: onError("Error add credential ${credential.providerId}", NullPointerException("Firebase user is null"))
                }
                .addOnFailureListener { onError("Error add credential ${credential.providerId}", it) }

    }
}
