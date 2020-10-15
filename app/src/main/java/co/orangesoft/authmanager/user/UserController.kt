package co.orangesoft.authmanager.user

import by.orangesoft.auth.user.BaseUserController

interface UserController: BaseUserController<Profile, Settings> {

    fun getRefreshToken(): String

    fun updateAccessToken(token: String)

    fun updateRefreshToken(token: String)

}