package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import by.orangesoft.auth.credentials.BaseCredential
import by.orangesoft.auth.user.BaseUserController

interface AuthManagerInterface<T : BaseUserController<*>> {

    fun getCurrentUser(): LiveData<T>

    fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>? = null)
    fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>.() -> Unit) = login(activity, method, AuthListener<T>(activity).apply(listener))

    suspend fun logout(listener: AuthListener<T>? = null)

    suspend fun deleteUser(listener: AuthListener<T>? = null)

    fun getCredentials(): LiveData<Set<BaseCredential>>

    fun addCredential(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>? = null)
    fun addCredential(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>.() -> Unit) = addCredential(activity, method, AuthListener<T>(activity).apply(listener))

    fun removeCredential(credential: BaseCredential, listener: AuthListener<T>? = null)
    fun removeCredential(credential: BaseCredential, listener: AuthListener<T>.() -> Unit) = removeCredential(credential, AuthListener<T>().apply(listener))

}