package by.orangesoft.auth.user

import by.orangesoft.auth.credentials.BaseCredential
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

internal object UserProvider {
    val currentUser: MutableStateFlow<out BaseUserController<*>> = MutableStateFlow(object : BaseUserController<Any> {
        override var profile: Any? = null
        override val credentials: MutableStateFlow<Set<BaseCredential>> = MutableStateFlow(setOf())

        override suspend fun update() {
            //do nothing
        }

        override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
            //do nothing
        }

        override suspend fun refresh() {
            //do nothing
        }

        override suspend fun getAccessToken(): String {
            return ""
        }

        override fun updateAccount(profile: Any?) {
            //do nothing
        }
    })
}