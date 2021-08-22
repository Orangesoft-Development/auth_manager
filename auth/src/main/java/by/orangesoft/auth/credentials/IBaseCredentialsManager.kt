package by.orangesoft.auth.credentials

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.flow.Flow
import java.lang.UnsupportedOperationException
import kotlin.jvm.Throws

interface IBaseCredentialsManager<T: BaseUserController<*>>  {

    /**
     * @param user if null - login process
     */
    fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?): Flow<T>

    fun removeCredential(credential: IBaseCredential, user: T): Flow<T>

    abstract class Builder(protected val credential: IBaseCredential) {

        @Throws(UnsupportedOperationException::class)
        protected abstract fun createCredential(): IBaseCredentialController

        @Throws(UnsupportedOperationException::class)
        open fun build(activity: FragmentActivity? = null): IBaseCredentialController =
             createCredential()
                     .apply { activity?.let { setActivity(it) } }

    }

}
