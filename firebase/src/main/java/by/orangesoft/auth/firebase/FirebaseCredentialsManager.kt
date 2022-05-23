package by.orangesoft.auth.firebase

import android.content.Context
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.credential.CredentialsEnum
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.Providers
import com.google.firebase.auth.FirebaseAuth
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.jvm.Throws

open class FirebaseCredentialsManager(private val appContext: Context, parentJob: Job? = null) :
    BaseCredentialsManager<FirebaseUserController>(parentJob) {

    protected val firebaseInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val credPaths by lazy { getCredentialPaths(appContext) }

    @Throws(Throwable::class)
    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController =
        getCurrentUser()

    override fun getCurrentUser(): FirebaseUserController = FirebaseUserController(firebaseInstance)

    private fun getCredentialPaths(appContext: Context): ArrayList<String> {
        val credentials = arrayListOf<String>()
        val optimizedDirectory = appContext.getDir(OUTDEX_NAME, 0).absolutePath
        val classLoader = DexClassLoader(
            appContext.packageCodePath,
            optimizedDirectory,
            null,
            DexClassLoader.getSystemClassLoader()
        )
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

    private fun newInstanceOfCredController(
        credentialsEnum: CredentialsEnum,
        googleCredential: FirebaseAuthCredential.Google? = null,
        phoneCredential: FirebaseAuthCredential.Phone? = null
    ): IBaseCredentialController {
        val className = credPaths.firstOrNull { it == credentialsEnum.className }
            ?: throw UnsupportedOperationException("Method is not supported")
        val clazz: Class<*> = Class.forName(className)
        val o = when (className) {
            CredentialsEnum.GOOGLE.className -> {
                clazz.getConstructor(FirebaseAuthCredential.Google::class.java)
                    .newInstance(googleCredential)
            }
            CredentialsEnum.PHONE.className -> {
                clazz.getConstructor(FirebaseAuthCredential.Phone::class.java)
                    .newInstance(phoneCredential)
            }
            else -> {
                clazz.newInstance()
            }
        }

        return o as IBaseCredentialController
    }

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

    override suspend fun onUserLogout(user: FirebaseUserController): FirebaseUserController {
        signOut()
        return getCurrentUser()
    }

    override suspend fun onUserDelete(user: FirebaseUserController): FirebaseUserController {
        signOut()
        firebaseInstance.currentUser?.delete()?.await()
        return getCurrentUser()
    }

    fun signInAnonymously(): Flow<FirebaseUserController> {
        launch {
            firebaseInstance.signInAnonymously().await()
        }
        return getUpdatedUserFlow()
    }

    override fun removeCredential(
        credential: IBaseCredential,
        user: FirebaseUserController
    ): Flow<FirebaseUserController> {
        clearCredInfo(credential)
        return super.removeCredential(credential, user).onEach {
            user.reloadCredentials()
        }
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder =
        CredBuilder(credential)

    override fun signOut() {
        singOutAllCredController()
        firebaseInstance.signOut()
        firebaseInstance.currentUser?.providerData?.clear()
    }

    override fun clearCredInfo(credential: IBaseCredential, force: Boolean) {
        getCurrentCredController(credential, force)?.clearCredInfo(appContext)
    }

    private fun singOutAllCredController() {
        getCurrentUser().credentials.value.forEach {
            when (it.providerId) {
                Providers.APPLE -> FirebaseAuthCredential.Apple
                Providers.FACEBOOK -> FirebaseAuthCredential.Facebook
                Providers.GOOGLE -> FirebaseAuthCredential.Google()
                Providers.PHONE -> FirebaseAuthCredential.Phone()
                else -> null
            }?.let { authCred -> clearCredInfo(authCred) }
        }
    }

    private fun getCurrentCredController(
        credential: IBaseCredential,
        force: Boolean = false
    ): IBaseCredentialController? =
        if (force || getCurrentUser().credentials.value.firstOrNull { it.providerId == credential.providerId } != null) {
            getBuilder(credential).build()
        } else null

    open inner class CredBuilder(credential: IBaseCredential) :
        IBaseCredentialsManager.Builder(credential) {

        override fun createCredential(): IBaseCredentialController {
            return when (val firebaseCredential = credential) {
                is FirebaseAuthCredential.Apple -> newInstanceOfCredController(CredentialsEnum.APPLE)
                is FirebaseAuthCredential.Google -> newInstanceOfCredController(
                    CredentialsEnum.GOOGLE,
                    googleCredential = firebaseCredential
                )
                is FirebaseAuthCredential.Facebook -> newInstanceOfCredController(CredentialsEnum.FACEBOOK)
                is FirebaseAuthCredential.Phone -> newInstanceOfCredController(
                    CredentialsEnum.PHONE,
                    phoneCredential = firebaseCredential
                )
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        }
    }

    companion object {
        private const val OUTDEX_NAME = "outdex"
    }

}