package by.orangesoft.auth.firebase

import android.content.Context
import by.orangesoft.auth.credentials.*
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

    private fun getCredentialPaths(appContext: Context): ArrayList<String> {
        val credentials = arrayListOf<String>()
        val optimizedDirectory = appContext.getDir("outdex", 0).absolutePath
        val classLoader = DexClassLoader(appContext.packageCodePath, optimizedDirectory, null, DexClassLoader.getSystemClassLoader())
        if (loadClass(classLoader, APPLE_CLASS_NAME)) { credentials.add(APPLE_CLASS_NAME) }
        if (loadClass(classLoader, GOOGLE_CLASS_NAME)) { credentials.add(GOOGLE_CLASS_NAME) }
        if (loadClass(classLoader, FACEBOOK_CLASS_NAME)) { credentials.add(FACEBOOK_CLASS_NAME) }

        return credentials
    }

    private fun loadClass(classLoader: DexClassLoader, className: String): Boolean {
        return try {
            classLoader.loadClass(className)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun newInstanceOfCredController(classNameWithPath: String?, credential: Firebase.Google? = null): IBaseCredentialController {
        if (classNameWithPath == null) {
            throw UnsupportedOperationException("Method is not supported")
        }
        val clazz: Class<*> = Class.forName(classNameWithPath)
        val o = if (classNameWithPath.contains(GOOGLE_CLASS_NAME, true)) {
            clazz.getConstructor(Firebase.Google::class.java)
                .newInstance(credential)
        } else {
            clazz.newInstance()
        }

        return o as IBaseCredentialController
    }

    @Throws(Throwable::class)
    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
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
                is Firebase.Apple -> newInstanceOfCredController(credPaths.firstOrNull { it.contains(APPLE_CLASS_NAME) })
                is Firebase.Google -> newInstanceOfCredController(credPaths.firstOrNull { it.contains(GOOGLE_CLASS_NAME) }, credential)
                is Firebase.Facebook -> newInstanceOfCredController(credPaths.firstOrNull { it.contains(FACEBOOK_CLASS_NAME) })
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        }
    }

    companion object {
        private const val APPLE_CLASS_NAME = "co.orangesoft.apple.AppleCredentialController"
        private const val GOOGLE_CLASS_NAME = "co.orangesoft.google.GoogleCredentialController"
        private const val FACEBOOK_CLASS_NAME = "co.orangesoft.facebook.FacebookCredentialController"
    }
}