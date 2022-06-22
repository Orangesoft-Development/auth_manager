package by.orangesoft.auth.user

import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.credentials.CredentialResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.io.File
import kotlin.jvm.Throws

abstract class BaseUserController<P> {

    abstract val profile: P
    abstract val credentials: StateFlow<Collection<CredentialResult>>

    @Throws(Throwable::class)
    protected abstract suspend fun reload()

    @Throws(Throwable::class)
    protected abstract suspend fun updateAvatar(file: File)

    @Throws(Throwable::class)
    protected abstract suspend fun updateAccount(profile: P)

    fun reloadProfile(): Flow<P> =
        getProfileAfterUpdateFlow { reload() }

    fun updateProfileAvatar(file: File): Flow<P> =
        getProfileAfterUpdateFlow { updateAvatar(file) }

    fun updateCurrentProfileAccount(): Flow<P> =
        getProfileAfterUpdateFlow { updateAccount(profile) }

    fun updateProfileAccount(profile: P): Flow<P> =
        getProfileAfterUpdateFlow { updateAccount(profile) }

    fun containsCredential(authCredential: BaseAuthCredential) =
        credentials.value.let { creds -> creds.firstOrNull { it.providerId == authCredential.providerId } != null }

    fun isSingleCredential() = credentials.value.size == 1

    private fun getProfileAfterUpdateFlow(request: suspend () -> Unit): Flow<P> =
        flow {
            request()
            emit(profile)
        }

}