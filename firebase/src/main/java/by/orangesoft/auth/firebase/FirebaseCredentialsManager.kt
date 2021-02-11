package by.orangesoft.auth.firebase

import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.credential.controllers.AppleCredentialController
import by.orangesoft.auth.firebase.credential.controllers.FacebookCredentialController
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.controllers.GoogleCredentialController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import kotlin.NoSuchElementException
import kotlin.jvm.Throws

@InternalCoroutinesApi
open class FirebaseCredentialsManager(parentJob: Job? = null): BaseCredentialsManager<FirebaseUserController>(parentJob) {

    protected val firebaseInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    @Throws(Throwable::class)
    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController = getCurrentUser()

    protected open fun getCurrentUser(): FirebaseUserController = FirebaseUserController(firebaseInstance)

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

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CredBuilder(credential)

    open inner class CredBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder(credential) {

        override fun createCredential(): IBaseCredentialController =
            when (credential) {
                is Firebase.Apple       -> AppleCredentialController()
                is Firebase.Google      -> GoogleCredentialController(credential)
                is Firebase.Facebook    -> FacebookCredentialController()
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
    }
}