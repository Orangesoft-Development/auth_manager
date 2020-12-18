package by.orangesoft.auth.credentials

import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.user.IBaseUserController
import java.lang.UnsupportedOperationException
import kotlin.jvm.Throws

interface IBaseCredentialsManager<T: IBaseUserController<*>>  {

    /**
     * @param user if null - login process
     */
    fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?)

    fun removeCredential(user: T, credential: IBaseCredential)

    abstract class Builder(protected val credential: IBaseCredential) {

        @Throws(UnsupportedOperationException::class)
        protected abstract fun createCredential(): IBaseCredentialController

        @Throws(UnsupportedOperationException::class)
        open fun build(activity: FragmentActivity? = null): IBaseCredentialController =
             createCredential()
                     .apply { activity?.let { setActivity(it) } }

    }

}