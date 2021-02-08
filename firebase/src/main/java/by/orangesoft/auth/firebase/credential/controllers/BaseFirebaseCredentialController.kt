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

abstract class BaseFirebaseCredentialController(override val credential: Firebase): IBaseCredentialController {

    protected val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    protected lateinit var activityCallback: Task<AuthResult>

    private lateinit var flow: MutableSharedFlow<*>
    private val flowJob: Job by lazy { flow.launchIn(CoroutineScope(Dispatchers.IO)) }

    override fun addCredential() : Flow<CredentialResult> {
        return MutableSharedFlow<CredentialResult>(1, 1).apply {
            flow = this
            getCredential()
        }.asSharedFlow()
    }

    override fun removeCredential(): Collection<IBaseCredential> {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == credential.providerId
        }?.let { provider ->
            runBlocking {
                authInstance.currentUser?.unlink(provider.providerId)?.await()
            }
        }

        return authInstance.getCredentials()
    }


    protected open fun onError(error: CancellationException) {
        flowJob.cancel(error)
    }

    protected open fun onError(message: String, cause: Throwable) {
        flowJob.cancel(message, cause)
    }

    protected fun getAuthTask(credential: AuthCredential): Task<AuthResult> =
        authInstance.currentUser?.let { currentUser ->
            if(!currentUser.isAnonymous)
                currentUser.linkWithCredential(credential)
            else null
        } ?: authInstance.signInWithCredential(credential)


    @Suppress("UNCHECKED_CAST")
    protected fun getCredential() {
        authInstance.currentUser?.let { user ->
            user.providerData.firstOrNull { it.providerId == credential.providerId }?.let {
                user.getIdToken(true)
                    .addOnSuccessListener { (flow as MutableSharedFlow<CredentialResult>).tryEmit(CredentialResult(credential, it.token ?: "")) }
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
                        ?.addOnSuccessListener { (flow as MutableSharedFlow<CredentialResult>).tryEmit(CredentialResult(credential, it.token ?: "")) }
                        ?.addOnFailureListener {
                            authInstance.signOut()
                            onError("Error add credential ${credential.providerId}", it)
                        } ?: onError("Error add credential ${credential.providerId}", NullPointerException("Firebase user is null"))
                }
                .addOnFailureListener { onError("Error add credential ${credential.providerId}", it) }

    }
}