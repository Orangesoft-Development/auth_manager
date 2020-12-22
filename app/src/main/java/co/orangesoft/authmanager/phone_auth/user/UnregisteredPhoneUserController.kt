package co.orangesoft.authmanager.phone_auth.user

import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.user.IBaseUserController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class UnregisteredPhoneUserController : PhoneUserController(null) {
    override val profile: PhoneProfile = PhoneProfile("")
    override val credentials: StateFlow<Set<IBaseCredential>> by lazy {
        MutableStateFlow(setOf())
    }

    override suspend fun reload(onError: ((Throwable) -> Unit)?) {
        //do nothing
    }

    override suspend fun updateAvatar(file: File, onError: ((Throwable) -> Unit)?) {
        //do nothing
    }

    override suspend fun updateAccount(profile: PhoneProfile, onError: ((Throwable) -> Unit)?) {
        //do nothing
    }

    override suspend fun saveChanges(onError: ((Throwable) -> Unit)?) {
        //do nothing
    }
}