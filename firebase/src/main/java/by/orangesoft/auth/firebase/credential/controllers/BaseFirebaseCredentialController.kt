package by.orangesoft.auth.firebase.credential.controllers

import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialController
import by.orangesoft.auth.credentials.UnlinkCredentialResult
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.UpdateCredAuthResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

abstract class BaseFirebaseCredentialController(override val authCredential: FirebaseAuthCredential): IBaseCredentialController, CoroutineScope {

    protected val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var credResultFlow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)
    protected var authTaskFlow: MutableSharedFlow<Task<out AuthResult>> = MutableSharedFlow(1, 1)

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun addCredential(): Flow<CredentialResult> = credResultFlow.onStart { getCredential(currentCoroutineContext()) }

    override fun removeCredential(): Flow<CredentialResult> = credResultFlow.onStart { unlinkCurrentProvider() }

    protected open fun onError(error: CancellationException) {
        coroutineContext.cancel(error)
    }

    protected open fun onError(message: String, cause: Throwable) {
        coroutineContext.job.cancel(message, cause.convertToNormalExceptionType())
    }

    protected fun emitAuthTask(credential: AuthCredential) {
        authTaskFlow.tryEmit(authInstance.currentUser?.let { currentUser ->
            val authTask = if (!currentUser.isAnonymous) {
                currentUser.providerData.firstOrNull { it.providerId == authCredential.providerId }?.let {
                    updateCurrentCredential(currentUser, credential)
                } ?: currentUser.linkWithCredential(credential)
            } else null
            authTask
        } ?: authInstance.signInWithCredential(credential))
    }

    protected open suspend fun getCredential(coroutineContext: CoroutineContext) {
        authTaskFlow.onEach { task ->
            credResultFlow.tryEmit(
                CredentialResult(authCredential.providerId,
                                 getToken(task.await().user
                                              ?: throw KotlinNullPointerException("FirebaseUser cannot be null"))))
        }
            .catch {
                coroutineContext.job.cancel("Error add credential ${authCredential.providerId}", it.convertToNormalExceptionType())
            }
            .onCompletion {
                it?.let {
                    coroutineContext.cancel(CancellationException(it.message, it.cause))
                }
            }
            .launchIn(CoroutineScope(coroutineContext + this.coroutineContext.job))
    }

    private suspend fun unlinkCurrentProvider() {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == authCredential.providerId
        }?.let { provider ->
            authInstance.currentUser?.unlink(provider.providerId)?.await()
            credResultFlow.emit(UnlinkCredentialResult())
        } ?: throw NoSuchElementException("Cannot remove method ${authCredential.providerId}")
    }

    private suspend fun getToken(user: FirebaseUser): String = user.getIdToken(true).await().token
        ?: throw KotlinNullPointerException("Token must not be null")

    //TODO update deprecated method
    protected open fun updateCurrentCredential(user: FirebaseUser, authCredential: AuthCredential) : Task<UpdateCredAuthResult> =
        Tasks.call { UpdateCredAuthResult(user, authCredential) }

    private fun Throwable.convertToNormalExceptionType(): Throwable =
        if ((this is FirebaseAuthWebException && this.message?.contains("canceled by the user", true) == true)
            || (this is ApiException && this.statusCode == CANCEL_API_ERROR)) {
            CancellationException("${authCredential.providerId} canceled by user")
        } else  this

    companion object {
        const val CANCEL_API_ERROR = 12501
    }

}
