package by.orangesoft.auth.user

import androidx.lifecycle.LiveData
import by.orangesoft.auth.credentials.BaseCredential
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.File

interface BaseUserController<T> {

    val profile: T?
    val credentials: LiveData<Set<BaseCredential>>

    suspend fun update()
    suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit)
    suspend fun refresh()

    suspend fun getAccessToken(): String

    fun updateAccount(function: (UserProfileChangeRequest.Builder) -> Unit)
}