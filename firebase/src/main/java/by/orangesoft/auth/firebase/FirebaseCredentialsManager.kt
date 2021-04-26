package by.orangesoft.auth.firebase

import android.content.Context
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.credential.CredentialsEnum
import by.orangesoft.auth.firebase.credential.Firebase
import com.google.firebase.auth.FirebaseAuth
import dalvik.system.DexClassLoader
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import java.util.*

@InternalCoroutinesApi
open class FirebaseCredentialsManager(appContext: Context, parentJob: Job? = null): BaseCredentialsManager<FirebaseUserController>(parentJob) {

    protected val firebaseInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val credPaths by lazy { getCredentialPaths(appContext) }

    @Throws(Throwable::class)
    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController = getCurrentUser()

    protected open fun getCurrentUser(): FirebaseUserController = FirebaseUserController(firebaseInstance)

    private fun getCredentialPaths(appContext: Context): ArrayList<String> {
        val credentials = arrayListOf<String>()
        val optimizedDirectory = appContext.getDir(OUTDEX_NAME, 0).absolutePath
        val classLoader = DexClassLoader(appContext.packageCodePath, optimizedDirectory, null, DexClassLoader.getSystemClassLoader())
        loadClassIfExist(classLoader) { className ->
            credentials.add(className)
        }

        return credentials
    }

    private fun loadClassIfExist(classLoader: DexClassLoader, callback: (String) -> Unit) {
        for (credential in CredentialsEnum.getCredentialsPaths()) {
            try {
                callback.invoke(classLoader.loadClass(credential.className).name)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun newInstanceOfCredController(credentialsEnum: CredentialsEnum,
                                            googleCredential: Firebase.Google? = null,
                                            phoneCredential: Firebase.Phone? = null): IBaseCredentialController {
        val className = credPaths.firstOrNull { it == credentialsEnum.className } ?: throw UnsupportedOperationException("Method is not supported")
        val clazz: Class<*> = Class.forName(className)
        val o = when (className) {
            CredentialsEnum.GOOGLE.className -> {
                clazz.getConstructor(Firebase.Google::class.java)
                    .newInstance(googleCredential)
            }
            CredentialsEnum.PHONE.className -> {
                clazz.getConstructor(Firebase.Phone::class.java)
                    .newInstance(phoneCredential)
            }
            else -> {
                clazz.newInstance()
            }
        }

        return o as IBaseCredentialController
    }

    @Throws(Throwable::class)
    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
        user.reloadCredentials()
    }

    @Throws(Throwable::class)
    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: FirebaseUserController) {
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
                is Firebase.Apple -> newInstanceOfCredController(CredentialsEnum.APPLE)
                is Firebase.Google -> newInstanceOfCredController(CredentialsEnum.GOOGLE, googleCredential = credential)
                is Firebase.Facebook -> newInstanceOfCredController(CredentialsEnum.FACEBOOK)
                is Firebase.Phone -> newInstanceOfCredController(CredentialsEnum.PHONE, phoneCredential = credential)
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        }
    }

    companion object {
        private const val OUTDEX_NAME = "outdex"
    }
}