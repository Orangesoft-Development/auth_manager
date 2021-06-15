package by.orangesoft.auth.user

import by.orangesoft.auth.credentials.CredentialResult
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import kotlin.jvm.Throws

interface IBaseUserController<P> {

    val profile: P
    val credentials: StateFlow<Collection<CredentialResult>>

    @Throws(Throwable::class)
    suspend fun reload()

    @Throws(Throwable::class)
    suspend fun updateAvatar(file: File)

    @Throws(Throwable::class)
    suspend fun updateAccount(profile: P)

    @Throws(Throwable::class)
    suspend fun saveChanges()




}