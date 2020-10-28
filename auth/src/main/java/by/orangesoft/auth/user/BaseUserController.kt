package by.orangesoft.auth.user

import com.google.firebase.auth.UserProfileChangeRequest
import java.io.File

interface BaseUserController<T> {

    val profile: T?

    suspend fun update()
    suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit)
    suspend fun refresh()

    suspend fun getAccessToken(): String

    fun updateAccount(function: (UserProfileChangeRequest.Builder) -> Unit)
}