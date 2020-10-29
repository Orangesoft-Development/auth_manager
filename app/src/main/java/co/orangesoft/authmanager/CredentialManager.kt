package co.orangesoft.authmanager

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.Firebase
import by.orangesoft.auth.credentials.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.user.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

internal class CredentialManager(
    private val authService: AuthService
): FirebaseCredentialsManager<FirebaseUserController<Profile>>() {

    override fun getLoggedUser(): FirebaseUserController<Profile> =
        firebaseInstance.currentUser?.let {
            UserControllerImpl(firebaseInstance)
        } ?: UnregisteredUserControllerImpl(firebaseInstance)

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController<Profile> {

        try {
            val profileResponse = authService.login(credentialResult)
            if (profileResponse.isSuccessful) {

                profileResponse.body()?.let { profile ->
                    firebaseInstance.currentUser?.let { updateFirebaseUser(it, profile) }
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
        //TODO what should do with profile here
        val profileResponse = authService.addCreds(user.getAccessToken(), credentialResult.method.providerId)
        (credentials as MutableLiveData).postValue(getCredentials())
    }

    override suspend fun onCredentialRemoved(credential: FirebaseCredential, user: FirebaseUserController<Profile>) {
        //TODO what should do with profile here
        val profileResponse = authService.removeCreds(user.getAccessToken(), credential.providerId.replace(".com", ""))
        (credentials as MutableLiveData).postValue(getCredentials())
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
        (credentials as MutableLiveData).postValue(getCredentials())
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
                    (credentials as MutableLiveData).postValue(getCredentials())
                    listener?.invoke(getLoggedUser())
                } else {
                    listener?.invoke(Throwable(response.message()))
                }
            }

        } catch (e: Exception) {
            listener?.invoke(e)
        }
    }

    //TODO check the same thing in updateAccount() in FirebaseUserController
    private fun updateFirebaseUser(user: FirebaseUser, profile: Profile) {
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .apply {
                    displayName = profile.name
                    photoUri = Uri.parse(profile.avatarUrl ?: "")
                }.build()
        ).addOnFailureListener { Log.e(TAG, "Unable update firebase profile", it) }

        (credentials as MutableLiveData).postValue(getCredentials())
    }

    override fun createUserController(credentialResult: CredentialResult, firebaseInstance: FirebaseAuth): FirebaseUserController<Profile> = getLoggedUser()

    override fun getBuilder(method: AuthMethod): Builder =
        CredBuilder(method)

    override fun getBuilder(credential: FirebaseCredential): Builder =
        CredBuilder(
            when (credential.providerId) {
                Firebase.Apple.providerId -> Firebase.Apple
                Firebase.Facebook.providerId -> Firebase.Facebook
                Firebase.Google("").providerId -> Firebase.Google("")
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        )
}