package co.orangesoft.authmanager.user

import by.orangesoft.auth.user.BaseUserController

class Profile(
    val id: String,
    var name: String? = null,
    var avatarUrl: String? = null,
    var birthday: String? = null
)

class Settings(
    var customSetting1: String? = null,
    var customSetting2: String? = null
): BaseUserController.UserSettings