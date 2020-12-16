package co.orangesoft.authmanager.firebase_auth

import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialsManager
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.user.*

internal class CredentialManager(
    private val authService: AuthService,
    private val profileService: ProfileService
): FirebaseCredentialsManager() {

    override fun getCurrentUser(): FirebaseUserController =
        firebaseInstance.currentUser?.let {
            UserControllerImpl(firebaseInstance, profileService)
        } ?: UnregisteredUserControllerImpl(firebaseInstance)

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController {
        lateinit var user: FirebaseUserController
        authService::login.parseResponse(credentialResult){
            onSuccess { user = super.onLogged(credentialResult).apply { updateAccount(profile) } }
            onError { throw it }
        }

        return user
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
        authService::addCreds.parseResponse(user.accessToken, credentialResult.credential.providerId){
            onSuccess { user.updateCredentials() }
            onError { listener?.invoke(it) }
        }
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: FirebaseUserController) {
        authService::removeCreds.parseResponse(user.accessToken, credential.providerId.replace(".com", "")){
            onSuccess { user.updateCredentials() }
            onError { listener?.invoke(it) }
        }
    }

    override suspend fun logout(user: FirebaseUserController) {
        authService::logout.parseResponse(user.accessToken){
            onSuccess { super.logout(user) }
            onError { listener?.invoke(it) }
        }
    }

    override suspend fun deleteUser(user: FirebaseUserController) {
        authService::delete.parseResponse(user.accessToken){
            onSuccess { super.deleteUser(user) }
            onError { listener?.invoke(it) }
        }
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder =
        CredBuilder(
            when (credential.providerId) {
                Firebase.Apple.providerId -> Firebase.Apple
                Firebase.Facebook.providerId -> Firebase.Facebook
                Firebase.Google("").providerId -> Firebase.Google("")
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        )
}