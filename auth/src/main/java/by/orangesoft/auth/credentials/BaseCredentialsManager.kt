package by.orangesoft.auth.credentials

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import by.orangesoft.auth.AuthListener
import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.UnsupportedOperationException
import kotlin.coroutines.CoroutineContext

abstract class BaseCredentialsManager<T: BaseUserController<*, *>, C: Any> (override val coroutineContext: CoroutineContext = Dispatchers.IO): CoroutineScope {

    protected val TAG = "CredentialsController"

    abstract val credentials: LiveData<Set<C>>

    protected val onCredentialException: (Throwable) -> Unit = {
        Log.e(TAG, "Credential exception: ", it)
        listener?.invoke(it)
    }

    protected var listener: AuthListener<T>? = null

    abstract fun getLoggedUser(): T?

    @Throws(Exception::class)
    protected abstract suspend fun onLogged(credentialResult: CredentialResult): T
    @Throws(Exception::class)
    protected abstract suspend fun onCredentialAdded(credentialResult: CredentialResult, user: T)
    @Throws(Exception::class)
    protected abstract suspend fun onCredentialRemoved(credential: C, user: T)

    protected abstract fun getBuilder(method: AuthMethod): Builder
    protected abstract fun getBuilder(credential: C): Builder

    open fun login(activity: FragmentActivity, method: AuthMethod) {
        getBuilder(method).build(activity).addCredential {
            onAddCredentialSuccess {
                launch {
                    try {
                        listener?.invoke(onLogged(it))
                    } catch (e: Exception){
                        onCredentialException.invoke(e)
                    }
                }
            }
            onCredentialException(onCredentialException)
        }
    }

    abstract fun logout(user: T)

    abstract fun deleteUser(user: T)

    open fun addCredential(activity: FragmentActivity, user: T, method: AuthMethod) {
        if(credentials.value?.firstOrNull { it.equals(method) } != null){
            listener?.invoke(user)
            return
        }

        getBuilder(method).build(activity).addCredential {
            onAddCredentialSuccess {
                launch {
                    try {
                        onCredentialAdded(it, user)
                        listener?.invoke(user)
                    } catch (e: Exception) {
                        onCredentialException.invoke(e)
                    }
                }
            }
            onCredentialException(onCredentialException)
        }
    }

    open fun removeCredential(user: T, credential: C) {

        if(credentials.value?.let { creds -> creds.firstOrNull { it.equals(credential) } != null && creds.size > 1 } != true){
            onCredentialException.invoke(NoSuchElementException("Cannot remove method $credential"))
            return
        }

        getBuilder(credential).build().removeCredential {
            onRemoveCredentialSuccess {
                launch {
                    try {
                        onCredentialRemoved(credential, user)
                        listener?.invoke(user)
                    } catch (e: Exception){
                        onCredentialException.invoke(e)
                    }
                }
            }
            onCredentialException(onCredentialException)
        }
    }

    open fun setAuthListener(listener: AuthListener<T>){
        this.listener = listener
    }

    open abstract class Builder(private val method: AuthMethod) {

        @Throws(UnsupportedOperationException::class)
        abstract protected fun createCredential(method: AuthMethod): BaseCredentialController

        @Throws(UnsupportedOperationException::class)
        fun build(activity: FragmentActivity? = null): BaseCredentialController {

            return createCredential(method).apply { activity?.let { setActivity(it) } }
        }
    }

}
