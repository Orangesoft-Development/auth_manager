package co.orangesoft.authmanager.firebase_auth

import android.net.Uri
import android.util.Log
import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.credentials.BaseCredential
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.Firebase
import by.orangesoft.auth.credentials.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.user.*
import co.orangesoft.authmanager.models.Profile
import com.google.firebase.auth.FirebaseAuth

internal class CredentialManager(
    private val authService: AuthService,
    private val profileService: ProfileService
): FirebaseCredentialsManager<FirebaseUserController<Profile>>() {

    override fun getLoggedUser(): FirebaseUserController<Profile> =
        firebaseInstance.currentUser?.let {
            UserControllerImpl(firebaseInstance, profileService)
        } ?: UnregisteredUserControllerImpl(firebaseInstance)

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController<Profile> {

        try {
            val profileResponse = authService.login(credentialResult)
            if (profileResponse.isSuccessful) {

                profileResponse.body()?.let { profile ->
                    getLoggedUser().updateAccount {
                        it.displayName = profile.name
                        it.photoUri = Uri.parse(profile.avatarUrl ?: "")
                    }
                }

                return createUserController(credentialResult, firebaseInstance)

            } else {
                firebaseInstance.signOut()
                throw Throwable(profileResponse.message())
            }

        } catch (e: Exception){
            firebaseInstance.signOut()
            throw e
        }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController<Profile>) {
        try {
            val profileResponse = authService.addCreds(user.getAccessToken(), credentialResult.method.providerId)
            if (profileResponse.isSuccessful) {
                user.updateCredentials()
            } else {
                Log.e(TAG, profileResponse.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    override suspend fun onCredentialRemoved(credential: BaseCredential, user: FirebaseUserController<Profile>) {
        try {
            val profileResponse = authService.removeCreds(user.getAccessToken(), credential.providerId.replace(".com", ""))
            if (profileResponse.isSuccessful) {
                user.updateCredentials()
            } else {
                Log.e(TAG, profileResponse.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    override suspend fun logout(user: FirebaseUserController<Profile>) {
        try {
            val response = authService.logout(user.getAccessToken())

            if (response.isSuccessful) {
                firebaseInstance.signOut()
            } else {
                Log.e(TAG, response.errorBody().toString())
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

        listener?.invoke(getLoggedUser())
        user.updateCredentials()
    }

    override suspend fun deleteUser(user: FirebaseUserController<Profile>) {
        try {
            if (user is UserControllerImpl) {
                val response = authService.delete(user.getAccessToken())
                if (response.isSuccessful) {
                    firebaseInstance.currentUser?.apply {
                        firebaseInstance.signOut()
                        delete()
                    }
                    user.updateCredentials()
                    listener?.invoke(getLoggedUser())
                } else {
                    listener?.invoke(Throwable(response.message()))
                }
            }

        } catch (e: Exception) {
            listener?.invoke(e)
        }
    }

    override fun createUserController(credentialResult: CredentialResult, firebaseInstance: FirebaseAuth): FirebaseUserController<Profile> = getLoggedUser()

    override fun getBuilder(method: AuthMethod): Builder =
        CredBuilder(method)

    override fun getBuilder(credential: BaseCredential): Builder =
        CredBuilder(
            when (credential.providerId) {
                Firebase.Apple.providerId -> Firebase.Apple
                Firebase.Facebook.providerId -> Firebase.Facebook
                Firebase.Google("").providerId -> Firebase.Google("")
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        )
}