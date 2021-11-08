package by.orangesoft.auth.user

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

    fun reloadProfile(): Flow<P> {
        return getProfileAfterUpdateFlow {
            reload()
        }
    }

    fun updateProfileAvatar(file: File): Flow<P> {
        return getProfileAfterUpdateFlow {
            updateAvatar(file)
        }
    }

    fun updateCurrentProfileAccount(): Flow<P> {
        return getProfileAfterUpdateFlow {
            updateAccount(profile)
        }
    }

    fun updateProfileAccount(profile: P): Flow<P> {
        return getProfileAfterUpdateFlow {
            updateAccount(profile)
        }
    }

    private fun getProfileAfterUpdateFlow(request: suspend () -> Unit): Flow<P> {
        return flow {
            request()
            emit(profile)
        }
    }

}