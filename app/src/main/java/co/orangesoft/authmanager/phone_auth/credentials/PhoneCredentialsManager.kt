package co.orangesoft.authmanager.phone_auth.credentials

import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.IBaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import by.orangesoft.auth.firebase.credential.Firebase
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.parseResponse
import co.orangesoft.authmanager.phone_auth.user.PhoneUserController

class PhoneCredentialsManager(private val authService: AuthService,
                              private val profileService: ProfileService
) : BaseCredentialsManager<PhoneUserController>() {


    override suspend fun onLogged(credentialResult: CredentialResult): PhoneUserController {
        lateinit var user: PhoneUserController
        authService::login.parseResponse(credentialResult){
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

    //TODO remove firebase from this
    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder {
        return FirebaseCredentialsManager.CredBuilder(PhoneAuthCredential())
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
}