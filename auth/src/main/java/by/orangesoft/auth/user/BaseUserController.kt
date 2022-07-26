package by.orangesoft.auth.user

import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.credentials.CredentialResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.io.File
import kotlin.jvm.Throws

/**
 * This is an abstract class of user controllers, contains the main methods of working with the user profile
 *
 * @see CredentialResult
 * @see BaseAuthCredential
 *
 */
abstract class BaseUserController<P> {

    abstract val profile: P
    abstract val credentials: StateFlow<Collection<CredentialResult>>

    /** Reload user profile */
    @Throws(Throwable::class)
    protected abstract suspend fun reload()

    /** Update user profile avatar
     * @param file Avatar file
     * */
    @Throws(Throwable::class)
    protected abstract suspend fun updateAvatar(file: File)

    /** Update user profile
     * @param profile New user profile file
     * */
    @Throws(Throwable::class)
    protected abstract suspend fun updateAccount(profile: P)

    /** Reload user profile and emit new profile value
     * @return Data stream with profile value
     * */
    fun reloadProfile(): Flow<P> =
        getProfileAfterUpdateFlow { reload() }

    /** Update user profile avatar and emit new profile value
     * @return Data stream with profile value
     * */
    fun updateProfileAvatar(file: File): Flow<P> =
        getProfileAfterUpdateFlow { updateAvatar(file) }

    /** Update current user profile and emit profile value
     * @return Data stream with profile value
     * */
    fun updateCurrentProfileAccount(): Flow<P> =
        getProfileAfterUpdateFlow { updateAccount(profile) }

    /** Update user profile and emit new profile value
     * @return Data stream with profile value
     * */
    fun updateProfileAccount(profile: P): Flow<P> =
        getProfileAfterUpdateFlow { updateAccount(profile) }

    /** Check whether the user's credentials contain the specified authCredential
     * @param authCredential Credential type
     * @see BaseAuthCredential
     * @return boolean value - does credentials contain the specified value
     * */
    fun containsCredential(authCredential: BaseAuthCredential) =
        credentials.value.let { creds -> creds.firstOrNull { it.providerId == authCredential.providerId } != null }

    /** Check whether the user's credentials contain only one credential type
     * @return boolean value - does credentials contain only one credential type
     * */
    fun isSingleCredential() = credentials.value.size == 1

    private fun getProfileAfterUpdateFlow(request: suspend () -> Unit): Flow<P> =
        flow {
            request()
            emit(profile)
        }

}