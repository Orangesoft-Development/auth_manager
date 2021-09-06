package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IAuthManager<T : BaseUserController<*>> {

    val currentUser: StateFlow<T>

    fun logoutFlow(): Flow<T>
    fun logout(): Job

    fun deleteUserFlow(): Flow<T>
    fun deleteUser(): Job

    fun login(activity: FragmentActivity, credential: BaseAuthCredential): Job
    fun loginFlow(activity: FragmentActivity, credential: BaseAuthCredential): Flow<T>

    fun addCredential(activity: FragmentActivity, credential: BaseAuthCredential): Job
    fun addCredentialFlow(activity: FragmentActivity, credential: BaseAuthCredential): Flow<T>

    fun removeCredential(credential: IBaseCredential): Job
    fun removeCredentialFlow(credential: IBaseCredential): Flow<T>

}