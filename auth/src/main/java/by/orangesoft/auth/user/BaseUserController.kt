package by.orangesoft.auth.user

import java.io.File

interface BaseUserController<U, S: BaseUserController.UserSettings> {

    val profile: U

    val settings: S

    fun update()
    fun updateAvatar(file: File, listener: (Throwable?) -> Unit)
    fun refresh()

    fun getAccessToken(): String


    interface UserSettings

}