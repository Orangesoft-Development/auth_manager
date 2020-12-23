package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.user.IBaseUserController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseAuthManager<T: IBaseUserController<*>, C: BaseCredentialsManager<T>>(protected val credentialsManager: C): IAuthManager<T> {

    protected abstract val user: MutableStateFlow<T>

    override val currentUser: StateFlow<T> by lazy { user.asStateFlow() }

    protected var authListener: AuthListener<T>? = null

    protected open val onAuthSuccessListener: (T) -> Unit = {
        user.value = it

        synchronized(this@BaseAuthManager) {
            authListener?.invoke(it)
            authListener = null
        }
    }

    protected open val onAuthErrorListener: (Throwable) -> Unit = {
        synchronized(this@BaseAuthManager) {
            authListener?.invoke(it)
            authListener = null
        }
    }

    private val credentialListener: AuthListener<T> = AuthListener(coroutineContext = Dispatchers.IO) {
        onAuthSuccess(onAuthSuccessListener)
        onAuthException(onAuthErrorListener)
    }

    init {
        credentialsManager.setAuthListener(credentialListener)
    }

    override fun login(activity: FragmentActivity, credential: AuthCredential, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.addCredential(activity, credential, null)
    }

    override fun addCredential(
            activity: FragmentActivity,
            credential: AuthCredential,
            listener: AuthListener<T>?
    ) {
        authListener = listener
        credentialsManager.addCredential(activity, credential, currentUser.value)
    }

    override fun removeCredential(credential: IBaseCredential, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.removeCredential(currentUser.value, credential)
    }
}
