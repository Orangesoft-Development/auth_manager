package by.orangesoft.auth.firebase

import android.content.Context
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.credential.Firebase
import com.google.firebase.auth.FirebaseAuth
import dalvik.system.DexFile
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*

@InternalCoroutinesApi
open class FirebaseCredentialsManager(appContext: Context, parentJob: Job? = null): BaseCredentialsManager<FirebaseUserController>(
    parentJob
) {

    protected val firebaseInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val credPaths by lazy { getCredentialPaths(appContext) }

    @Throws(Throwable::class)
    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController = getCurrentUser()

    protected open fun getCurrentUser(): FirebaseUserController = FirebaseUserController(
        firebaseInstance
    )

    @Throws(Throwable::class)
    override suspend fun onCredentialAdded(
        credentialResult: CredentialResult,
        user: FirebaseUserController
    ) {
        user.reloadCredentials()
    }

    @Throws(Throwable::class)
    override suspend fun onCredentialRemoved(
        credential: IBaseCredential,
        user: FirebaseUserController
    ) {
        user.reloadCredentials()
    }

    @Throws(Throwable::class)
    override suspend fun logout(user: FirebaseUserController) {
        firebaseInstance.signOut()
    }

    @Throws(Throwable::class)
    override suspend fun deleteUser(user: FirebaseUserController) {
        firebaseInstance.currentUser?.delete()?.await()
    }

    override fun removeCredential(credential: IBaseCredential, user: FirebaseUserController): Flow<FirebaseUserController> {
        return super.removeCredential(credential, user).onEach {
            user.reloadCredentials()
        }
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CredBuilder(
        credential
    )

    open inner class CredBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder(credential) {

        override fun createCredential(): IBaseCredentialController {

            return when (credential) {
                is Firebase.Apple -> newInstanceOfCredController(credPaths.first {
                    it.contains(
                        APPLE_CLASS_NAME,
                        true
                    )
                })
                is Firebase.Google -> newInstanceOfCredController(credPaths.first {
                    it.contains(
                        GOOGLE_CLASS_NAME,
                        true
                    )
                }, credential)
                is Firebase.Facebook -> newInstanceOfCredController(credPaths.first {
                    it.contains(
                        FACEBOOK_CLASS_NAME,
                        true
                    )
                })
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        }
    }

    companion object {

        private const val APPLE_CLASS_NAME = "AppleCredentialController"
        private const val GOOGLE_CLASS_NAME = "GoogleCredentialController"
        private const val FACEBOOK_CLASS_NAME = "FacebookCredentialController"

        @Suppress("DEPRECATION")
        fun getCredentialPaths(context: Context): ArrayList<String> {
            val credentials = arrayListOf<String>()

            try {
                val df = DexFile(context.packageCodePath)
                val iter: Enumeration<String> = df.entries()
                while (iter.hasMoreElements()) {
                    val type = iter.nextElement()

                    if (type.endsWith(APPLE_CLASS_NAME) ||
                        type.endsWith(GOOGLE_CLASS_NAME) ||
                        type.endsWith(FACEBOOK_CLASS_NAME)
                        ) {
                            credentials.add(type)
                        }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return credentials
        }

        fun newInstanceOfCredController(classNameWithPath: String, credential: Firebase.Google? = null): IBaseCredentialController {
            val clazz: Class<*> = Class.forName(classNameWithPath)
            val o = if (classNameWithPath.contains(GOOGLE_CLASS_NAME, true)) {
                clazz.getConstructor(Firebase.Google::class.java)
                    .newInstance(credential)
            } else {
                clazz.newInstance()
            }

            return o as IBaseCredentialController
        }
    }
}