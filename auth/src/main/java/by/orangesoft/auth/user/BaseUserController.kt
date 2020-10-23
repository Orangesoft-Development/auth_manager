package by.orangesoft.auth.user

import java.io.File

interface BaseUserController<U, S: BaseUserController.UserSettings> {

    val profile: U?

    val settings: S

    suspend fun update()
    suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit)
    suspend fun refresh()

    suspend fun getAccessToken(): String

    interface UserSettings
}