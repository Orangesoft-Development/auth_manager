package co.orangesoft.authmanager.user

import by.orangesoft.auth.user.UserController

interface SVBaseUserController: UserController<Profile, Settings> {

    fun getRefreshToken(): String

    fun updateAccessToken(token: String)

    fun updateRefreshToken(token: String)

}