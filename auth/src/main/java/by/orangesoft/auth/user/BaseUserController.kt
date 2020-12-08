package by.orangesoft.auth.user

import by.orangesoft.auth.credentials.BaseCredential
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface BaseUserController<P> {

    var profile: P?
    val credentials: StateFlow<Set<BaseCredential>>

    suspend fun update()
    suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit)
    suspend fun refresh()

    suspend fun getAccessToken(): String

    fun updateAccount(profile: P? = null)
}