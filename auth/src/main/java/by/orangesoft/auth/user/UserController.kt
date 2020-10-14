package by.orangesoft.auth.user

import java.io.File

interface UserController<U, S: UserController.UserSettings> {

    val profile: U

    val settings: S

    fun update()
    fun updateAvatar(file: File, listener: (Throwable?) -> Unit)
    fun refresh()

    fun getAccessToken(): String


    interface UserSettings

}