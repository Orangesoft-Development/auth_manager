package by.orangesoft.auth.firebase.credential.controllers

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.CredentialFragment
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

    protected var authTaskFlow: MutableSharedFlow<Task<out AuthResult>> = MutableSharedFlow(1, 1)
    private var credResultFlow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)

    private lateinit var credActivity: FragmentActivity
    private val credFragment by lazy { CredentialFragment.getInstance(this@BaseFirebaseCredentialController) }

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun addCredential(): Flow<CredentialResult> {
        return credResultFlow.onStart { getCredential(currentCoroutineContext()) }.take(1)
    }

    override fun removeCredential(): Flow<CredentialResult> {
        return credResultFlow.onStart { unlinkCurrentProvider() }.take(1)
    }

    protected open fun onError(error: CancellationException) {
        super.onError()
        coroutineContext.cancel(error)
    }

    protected open fun onError(message: String, cause: Throwable) {
        super.onError()
        coroutineContext.job.cancel(message, cause.convertToNormalExceptionType())
    }

    override fun onError() = removeCredentialFragment()

    override fun setActivity(activity: FragmentActivity) {
        super.setActivity(activity)
        credActivity = activity
        credActivity.supportFragmentManager.beginTransaction()
            .add(credFragment, CredentialFragment.TAG)
            .commit()
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
    protected open fun updateCurrentCredential(user: FirebaseUser, authCredential: AuthCredential) : Task<UpdateCredAuthResult>? =
        Tasks.call { UpdateCredAuthResult(user, authCredential) }

    private fun Throwable.convertToNormalExceptionType(): Throwable =
        if ((this is FirebaseAuthWebException && this.message?.contains("canceled by the user", true) == true)
            || (this is ApiException && this.statusCode == CANCEL_API_ERROR)) {
            CancellationException("${authCredential.providerId} canceled by user")
        } else  this


    private fun removeCredentialFragment() {
        if (::credActivity.isInitialized)
            credActivity.supportFragmentManager.beginTransaction().remove(credFragment).commit()
    }

    companion object {
        const val CANCEL_API_ERROR = 12501
    }

}