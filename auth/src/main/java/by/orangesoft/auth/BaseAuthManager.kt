package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.credentials.BaseCredential
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.user.BaseUserController
import by.orangesoft.auth.user.UserProvider
import kotlinx.coroutines.Dispatchers

abstract class BaseAuthManager<T: BaseUserController<*>>(protected val credentialsManager: BaseCredentialsManager<T>): AuthManagerInterface<T> {

    private var authListener: AuthListener<T>? = null

    @Suppress("UNCHECKED_CAST")
    final override fun getCurrentUser(): LiveData<T> {
        return UserProvider.currentUser as LiveData<T>
    }

    protected open val onAuthSuccessListener: (T) -> Unit = {
        (getCurrentUser() as MutableLiveData).postValue(it)

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
        (getCurrentUser() as MutableLiveData).postValue(credentialsManager.getLoggedUser())
    }

    override fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.login(activity, method)
    }

    override suspend fun logout(listener: AuthListener<T>?) {
        authListener = listener
        getCurrentUser().value?.let { credentialsManager.logout(it) }
    }

    override suspend fun deleteUser(listener: AuthListener<T>?) {
        authListener = listener
        getCurrentUser().value?.let { credentialsManager.deleteUser(it) }
    }

    override fun getCredentials(): LiveData<Set<BaseCredential>> {
        return getCurrentUser().value?.let {it.credentials } ?: throw KotlinNullPointerException("User does not exist")
    }

    override fun addCredential(
        activity: FragmentActivity,
        method: AuthMethod,
        listener: AuthListener<T>?
    ) {
        authListener = listener
        getCurrentUser().value?.let { credentialsManager.addCredential(activity, it, method) }
    }

    override fun removeCredential(credential: BaseCredential, listener: AuthListener<T>?) {
        authListener = listener
        getCurrentUser().value?.let { credentialsManager.removeCredential(it, credential) }
    }
}
