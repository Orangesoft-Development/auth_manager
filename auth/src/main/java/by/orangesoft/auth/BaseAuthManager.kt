package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseCredential
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.user.BaseUserController
import by.orangesoft.auth.user.UserProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseAuthManager<T: BaseUserController<*>>(protected val credentialsManager: BaseCredentialsManager<T>): AuthManagerInterface<T> {

    private var authListener: AuthListener<T>? = null

    @Suppress("UNCHECKED_CAST")
    final override fun getCurrentUser(): MutableStateFlow<T> {
        return UserProvider.currentUser as MutableStateFlow<T>
    }

    protected open val onAuthSuccessListener: (T) -> Unit = {
        getCurrentUser().value = it

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

    private val credentialListener: AuthListener<T> = AuthListener(Dispatchers.IO) {
        onAuthSuccess(onAuthSuccessListener)
        onAuthException(onAuthErrorListener)
    }

    init {
        credentialsManager.setAuthListener(credentialListener)
        credentialsManager.getLoggedUser()?.let { getCurrentUser().value = it }
    }

    override fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.login(activity, method)
    }

    override suspend fun logout(listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.logout(getCurrentUser().value)
    }

    override suspend fun deleteUser(listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.deleteUser(getCurrentUser().value)
    }

    override fun getCredentials(): MutableStateFlow<Set<BaseCredential>> {
        return getCurrentUser().value.credentials
    }

    override fun addCredential(
        activity: FragmentActivity,
        method: AuthMethod,
        listener: AuthListener<T>?
    ) {
        authListener = listener
        credentialsManager.addCredential(activity, getCurrentUser().value, method)
    }

    override fun removeCredential(credential: BaseCredential, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.removeCredential(getCurrentUser().value, credential)
    }
}
