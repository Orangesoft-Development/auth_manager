package by.orangesoft.auth.firebase

import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.credential.controllers.AppleCredentialController
import by.orangesoft.auth.firebase.credential.controllers.FacebookCredentialController
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.controllers.GoogleCredentialController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
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
    open suspend fun logout(user: FirebaseUserController) {
        firebaseInstance.signOut()
    }

    @Throws(Throwable::class)
    open suspend fun deleteUser(user: FirebaseUserController) {
        firebaseInstance.currentUser?.delete()?.await()
    }

    override fun removeCredential(credential: IBaseCredential, user: FirebaseUserController): Flow<FirebaseUserController> =
        flow {
            if(!user.credentials.value.let { creds -> creds.firstOrNull { it == credential } != null && creds.size > 1 }){
                throw NoSuchElementException("Cannot remove method $credential")
            }

            getBuilder(credential).build().removeCredential()
              .collectLatest {
                  user.reloadCredentials()
                  emit(user)
              }
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CredBuilder(credential)

    open class CredBuilder(credential: AuthCredential): IBaseCredentialsManager.Builder(credential) {
        constructor(credential: IBaseCredential): this(AuthCredential(credential))

        override fun createCredential(): IBaseCredentialController =
            when (credential) {
                is Firebase.Apple       -> AppleCredentialController()
                is Firebase.Google      -> GoogleCredentialController(credential)
                is Firebase.Facebook    -> FacebookCredentialController()
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
    }
}