package by.orangesoft.auth.credentials

import android.util.Log
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.AuthListener
import by.orangesoft.auth.user.IBaseUserController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws

abstract class BaseCredentialsManager<T: IBaseUserController<*>> (override val coroutineContext: CoroutineContext = Dispatchers.IO): CoroutineScope, IBaseCredentialsManager<T> {

    companion object {
        const val TAG = "CredentialsController"
    }

    protected val onCredentialException: (Throwable) -> Unit = {
        Log.e(TAG, "Credential exception: ", it)
        listener?.invoke(it)
    }

    protected var listener: AuthListener<T>? = null

    @Throws(Exception::class)
    protected abstract suspend fun onLogged(credentialResult: CredentialResult): T

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialAdded(credentialResult: CredentialResult, user: T)

    @Throws(Exception::class)
    protected abstract suspend fun onCredentialRemoved(credential: IBaseCredential, user: T)

    protected abstract fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder

    override fun addCredential(activity: FragmentActivity, credential: IBaseCredential, user: T?) {
        if (user?.credentials?.value?.firstOrNull { it == credential } != null) {
            listener?.invoke(user)
            return
        }

        getBuilder(credential).build(activity).addCredential {
            onAddCredentialSuccess {
                launch {
                    try {
                        if(user != null) {
                            onCredentialAdded(it, user)
                            listener?.invoke(user)
                        } else
                            listener?.invoke(onLogged(it))
                    } catch (e: Exception) {
                        onCredentialException.invoke(e)
                    }
                }
            }
            onCredentialException(onCredentialException)
        }
    }

    override fun removeCredential(user: T, credential: IBaseCredential) {
        if(!user.credentials.value.let { creds -> creds.firstOrNull { it == credential } != null && creds.size > 1 }) {
            onCredentialException.invoke(NoSuchElementException("Cannot remove method $credential"))
            return
        }

        getBuilder(credential).build().removeCredential {
            onRemoveCredentialSuccess {
                launch {
                    try {
                        onCredentialRemoved(credential, user)
                        listener?.invoke(user)
                    } catch (e: Exception){
                        onCredentialException.invoke(e)
                    }
                }
            }
            onCredentialException(onCredentialException)
        }
    }

    open fun setAuthListener(listener: AuthListener<T>){
        this.listener = listener
    }
}
