package by.orangesoft.auth.credentials.firebase

import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.credentials.BaseCredential
import by.orangesoft.auth.credentials.BaseCredentialController
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.credentials.firebase.controllers.AppleCredentialController
import by.orangesoft.auth.credentials.firebase.controllers.FacebookCredentialController
import by.orangesoft.auth.credentials.firebase.controllers.GoogleCredentialController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.NoSuchElementException

open class FirebaseCredentialsManager: BaseCredentialsManager<FirebaseUserController>() {

    protected val firebaseInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController {
        val user = getLoggedUser()
        user.updateCredentials()
        return user
    }

    override fun getLoggedUser(): FirebaseUserController {
        return FirebaseUserController(firebaseInstance = this.firebaseInstance)
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
        user.updateCredentials()
    }

    override suspend fun onCredentialRemoved(credential: BaseCredential, user: FirebaseUserController) {
        user.updateCredentials()
    }

    override suspend fun logout(user: FirebaseUserController) {
        launch { firebaseInstance.signOut() }
    }

    override suspend fun deleteUser(user: FirebaseUserController) {
        launch {
            firebaseInstance.currentUser?.delete()
            firebaseInstance.signOut()
        }
    }

    override fun removeCredential(user: FirebaseUserController, credential: BaseCredential) {
        if(user.credentials.value?.let { creds -> creds.firstOrNull { it.equals(credential) } != null && creds.size > 1 } != true){
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

    override fun getBuilder(method: AuthMethod): Builder = CredBuilder(method)
    override fun getBuilder(credential: BaseCredential): Builder = CredBuilder(
        when (credential.providerId) {
            Firebase.Apple.providerId               -> Firebase.Apple
            Firebase.Facebook.providerId            -> Firebase.Facebook
            Firebase.Google("").providerId  -> Firebase.Google("")
            else -> throw UnsupportedOperationException("Method $credential is not supported")
        }
    )

    open class CredBuilder(method: AuthMethod): Builder(method) {

        override fun createCredential(method: AuthMethod): BaseCredentialController =
            when (method) {
                is Firebase.Apple       -> AppleCredentialController()
                is Firebase.Google      -> GoogleCredentialController(method)
                is Firebase.Facebook    -> FacebookCredentialController()
                else -> throw UnsupportedOperationException("Method $method is not supported")
            }
    }
}