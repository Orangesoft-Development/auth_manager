package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import by.orangesoft.auth.credentials.firebase.FirebaseCredential
import by.orangesoft.auth.user.BaseUserController

interface AuthManagerInterface<T : BaseUserController<*>> {

    val currentUser: LiveData<T>

    fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>? = null)
    fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>.() -> Unit) = login(activity, method, AuthListener<T>(activity).apply(listener))

    suspend fun logout(listener: AuthListener<T>? = null)

    suspend fun deleteUser(listener: AuthListener<T>? = null)

    fun getCredentials(): LiveData<Set<FirebaseCredential>>

    fun addCredential(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>? = null)
    fun addCredential(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>.() -> Unit) = addCredential(activity, method, AuthListener<T>(activity).apply(listener))

    fun removeCredential(credential: FirebaseCredential, listener: AuthListener<T>? = null)
    fun removeCredential(credential: FirebaseCredential, listener: AuthListener<T>.() -> Unit) = removeCredential(credential, AuthListener<T>().apply(listener))

}