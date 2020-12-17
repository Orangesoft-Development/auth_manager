package by.orangesoft.auth.firebase

import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.credential.controllers.AppleCredentialController
import by.orangesoft.auth.firebase.credential.controllers.FacebookCredentialController
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.controllers.GoogleCredentialController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.NoSuchElementException

open class FirebaseCredentialsManager: BaseCredentialsManager<FirebaseUserController>() {

    protected val firebaseInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController = getCurrentUser()

    protected open fun getCurrentUser(): FirebaseUserController = FirebaseUserController(firebaseInstance)

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
        user.updateCredentials()
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: FirebaseUserController) {
        user.updateCredentials()
    }

    open suspend fun logout(user: FirebaseUserController) {
        firebaseInstance.signOut()
        listener?.invoke(getCurrentUser())
    }

    open suspend fun deleteUser(user: FirebaseUserController) {
        firebaseInstance.currentUser?.delete()?.await()
        listener?.invoke(getCurrentUser())
    }

    override fun removeCredential(user: FirebaseUserController, credential: IBaseCredential) {
        if(!user.credentials.value.let { creds -> creds.firstOrNull { it == credential } != null && creds.size > 1 }){
            onCredentialException.invoke(NoSuchElementException("Cannot remove method $credential"))
            return
        }

        getBuilder(credential).build().removeCredential {
            onRemoveCredentialSuccess {
                launch {
                    onCredentialRemoved(credential, user)
                    listener?.invoke(user)
                }
            }
            onCredentialException(onCredentialException)
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