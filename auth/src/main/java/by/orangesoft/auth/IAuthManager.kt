package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.user.IBaseUserController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.Throws

interface IAuthManager<T : IBaseUserController<*>> {

    val currentUser: StateFlow<T>

    fun login(activity: FragmentActivity, credential: AuthCredential): Job

    @Throws
    suspend fun logout()

    @Throws
    suspend fun deleteUser()

    fun addCredential(activity: FragmentActivity, credential: AuthCredential): Job

    fun removeCredential(credential: IBaseCredential): Job

}