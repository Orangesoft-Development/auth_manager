package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.Throws

interface IAuthManager<T : BaseUserController<*>> {

    val currentUser: StateFlow<T>

    fun login(activity: FragmentActivity, credential: BaseAuthCredential): Job

    @Throws
    suspend fun logout()

    @Throws
    suspend fun deleteUser()

    fun addCredential(activity: FragmentActivity, credential: BaseAuthCredential): Job

    fun removeCredential(credential: IBaseCredential): Job

}