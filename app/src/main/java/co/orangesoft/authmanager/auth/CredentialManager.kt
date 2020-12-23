package co.orangesoft.authmanager.auth

import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.auth.user.*
import co.orangesoft.authmanager.auth.phone_auth.credentials.PhoneAuthCredential
import co.orangesoft.authmanager.auth.phone_auth.credentials.PhoneCredentialController

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

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CustomCredBuilder(AuthCredential(credential))

    inner class CustomCredBuilder(credential: AuthCredential): CredBuilder(credential) {

        override fun createCredential(): IBaseCredentialController =
            when (credential) {
                is PhoneAuthCredential  -> PhoneCredentialController(authService, credential)
                else -> super.createCredential()
            }
    }
}