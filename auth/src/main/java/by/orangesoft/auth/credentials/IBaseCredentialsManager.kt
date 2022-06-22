package by.orangesoft.auth.credentials

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.flow.Flow
import java.lang.UnsupportedOperationException
import kotlin.jvm.Throws

/**
 * Interface for credential manager, contains the basic methods for working with BaseUserController
 *
 * @see BaseUserController
 *
 */

interface IBaseCredentialsManager<T : BaseUserController<*>> {

    /**
     * Add a new user authorization method or start login process
     *
     * @param activity FragmentActivity
     * @param user current user controller value, if null - start login process
     * @param credential authorization credentials type
     *
     * @return Data stream with the current type of BaseUserController
     *
     * @see IBaseCredential
     * @see BaseUserController
     *
     */
    fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?): Flow<T>

    /**
     * Remove current authorization method of an authorized user
     *
     * @param user current user controller value
     * @param credential authorization credentials type
     *
     * @return Data stream with the current type of BaseUserController
     *
     * @see IBaseCredential
     * @see BaseUserController
     *
     */
    fun removeCredential(credential: IBaseCredential, user: T): Flow<T>

    /**
     * Logging out of the current authorized user session
     *
     * @param user current user controller value
     *
     * @return Data stream with the current type of BaseUserController
     *
     * @see BaseUserController
     *
     */
    fun logout(user: T): Flow<T>

    /**
     * Deleting an authorized user with all credential
     *
     * @param user current user controller value
     *
     * @return Data stream with the current type of BaseUserController
     *
     * @see BaseUserController
     *
     */
    fun deleteUser(user: T): Flow<T>

    /**
     * Logging out of the current session of an authorized user
     * and clearing all saved user credentials info to prevent automatic login
     */
    fun signOutAllCredController() {}

    /**
     * Clearing saved user credentials info to prevent auto-login
     *
     * @param credential authorization credentials type
     * @param force is it necessary to clear all saved data for the current authentication method
     *
     * @see IBaseCredential
     */
    fun clearCredInfo(credential: IBaseCredential, force: Boolean = false) {}


    /**
     * Builder class for creating instance of IBaseCredentialController
     *
     * @param credential authorization credentials type
     *
     * @see IBaseCredentialController
     * */
    abstract class Builder(protected val credential: IBaseCredential) {

        @Throws(UnsupportedOperationException::class)
        protected abstract fun createCredential(): IBaseCredentialController

        /**
         * @param activity FragmentActivity
         *
         * @return instance of IBaseCredentialController
         *
         * @see IBaseCredentialController
         * */
        @Throws(UnsupportedOperationException::class)
        open fun build(activity: FragmentActivity? = null): IBaseCredentialController =
            createCredential()
                .apply { activity?.let { setActivity(it) } }
    }

}
