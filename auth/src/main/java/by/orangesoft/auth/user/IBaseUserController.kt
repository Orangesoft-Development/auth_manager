package by.orangesoft.auth.user

import by.orangesoft.auth.credentials.IBaseCredential
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface IBaseUserController<P> {

    val profile: P
    val credentials: StateFlow<Set<IBaseCredential>>

    suspend fun reload(onError: ((Throwable) -> Unit)? = null)

    suspend fun updateAvatar(file: File, onError: ((Throwable) -> Unit)? = null)

    suspend fun updateAccount(profile: P, onError: ((Throwable) -> Unit)? = null)

    suspend fun saveChanges(onError: ((Throwable) -> Unit)? = null)


}