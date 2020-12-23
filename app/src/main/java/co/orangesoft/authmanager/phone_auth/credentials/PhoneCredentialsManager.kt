package co.orangesoft.authmanager.phone_auth.credentials

import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.controllers.AppleCredentialController
import by.orangesoft.auth.firebase.credential.controllers.FacebookCredentialController
import by.orangesoft.auth.firebase.credential.controllers.GoogleCredentialController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.parseResponse
import co.orangesoft.authmanager.phone_auth.user.PhoneUserController

class PhoneCredentialsManager(private val authService: AuthService,
                              private val profileService: ProfileService
) : BaseCredentialsManager<PhoneUserController>() {

    override suspend fun onLogged(credentialResult: CredentialResult): PhoneUserController {
        lateinit var user: PhoneUserController
        authService::phoneLogin.parseResponse(credentialResult) {
            onSuccess { user = PhoneUserController(profileService).apply { updateAccount(profile) } }
            onError { throw it }
        }

        return user
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: PhoneUserController) {
        authService::addCreds.parseResponse(user.accessToken, credentialResult.credential.providerId){
            onSuccess { user.updateCredentials() }
            onError { listener?.invoke(it) }
        }
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: PhoneUserController) {
        authService::removeCreds.parseResponse(user.accessToken, credential.providerId.replace(".com", "")){
            onSuccess { user.updateCredentials() }
            onError { listener?.invoke(it) }
        }
    }

    override suspend fun logout(user: PhoneUserController) {
        authService::logout.parseResponse(user.accessToken){
            onSuccess { super.logout(user) }
            onError { listener?.invoke(it) }
        }
    }

    override suspend fun deleteUser(user: PhoneUserController) {
        authService::delete.parseResponse(user.accessToken){
            onSuccess { super.deleteUser(user) }
            onError { listener?.invoke(it) }
        }
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CredBuilder(credential)

    inner class CredBuilder(credential: AuthCredential): IBaseCredentialsManager.Builder(credential) {
        constructor(credential: IBaseCredential): this(AuthCredential(credential))

        override fun createCredential(): IBaseCredentialController =
            when (credential) {
                is PhoneAuthCredential -> PhoneCredentialController(authService, credential)
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
    }
}