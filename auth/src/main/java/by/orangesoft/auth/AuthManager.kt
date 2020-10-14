package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import by.orangesoft.auth.credentials.CredentialsManager
import by.orangesoft.auth.user.UserController
import kotlinx.coroutines.Dispatchers

abstract class AuthManager<T: UserController<*, *>, C: Any>(protected val credentialsManager: CredentialsManager<T, C>): BaseAuthManager<T, C> {

    override val currentUser: LiveData<T> = MutableLiveData()

    override val userCredentials: LiveData<Set<C>> by lazy { credentialsManager.credentials }

    private var authListener:  AuthListener<T>? = null

    private val credentialListener: AuthListener<T> = AuthListener(Dispatchers.IO) {
            onAuthSucces(onAuthSuccessListener)
            onAuthException(onAuthErrorListener)
        }

    protected open val onAuthSuccessListener: (T) -> Unit = {
            (currentUser as MutableLiveData).postValue(it)

            synchronized(this@AuthManager) {
                authListener?.invoke(it)
                authListener = null
            }
        }

    protected open val onAuthErrorListener: (Throwable) -> Unit = {
        synchronized(this@AuthManager) {
            authListener?.invoke(it)
            authListener = null
        }
    }

    init {
        credentialsManager.setAuthListener(credentialListener)
        (currentUser as MutableLiveData).postValue(credentialsManager.getLoggedUser())
    }

    override fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.login(activity, method)
    }

    override fun logout(listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.logout(currentUser.value!!)

    }

    override fun deleteUser(listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.deleteUser(currentUser.value!!)
    }

    override fun addCredential(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.addCredential(activity, currentUser.value!!, method)
    }

    override fun removeCredential(credential: C, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.removeCredential(currentUser.value!!, credential)
    }

}
