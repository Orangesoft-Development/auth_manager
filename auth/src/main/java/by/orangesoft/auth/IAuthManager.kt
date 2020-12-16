package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.user.IBaseUserController
import kotlinx.coroutines.flow.StateFlow

interface IAuthManager<T : IBaseUserController<*>> {

    val currentUser: StateFlow<T>

    fun login(activity: FragmentActivity, credential: AuthCredential, listener: AuthListener<T>? = null)
    fun login(activity: FragmentActivity, credential: AuthCredential, listener: AuthListener<T>.() -> Unit) = login(activity, credential, AuthListener<T>(activity).apply(listener))

    suspend fun logout(listener: AuthListener<T>? = null)

    suspend fun deleteUser(listener: AuthListener<T>? = null)

    fun addCredential(activity: FragmentActivity, credential: AuthCredential, listener: AuthListener<T>? = null)
    fun addCredential(activity: FragmentActivity, credential: AuthCredential, listener: AuthListener<T>.() -> Unit) = addCredential(activity, credential, AuthListener<T>(activity).apply(listener))

    fun removeCredential(credential: IBaseCredential, listener: AuthListener<T>? = null)
    fun removeCredential(credential: IBaseCredential, listener: AuthListener<T>.() -> Unit) = removeCredential(credential, AuthListener<T>().apply(listener))

}