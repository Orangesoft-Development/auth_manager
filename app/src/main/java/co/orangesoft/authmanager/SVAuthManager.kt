package co.orangesoft.authmanager

import android.accounts.AccountManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.AuthManager
import by.orangesoft.auth.credentials.CredentialsManager
import by.orangesoft.auth.credentials.firebase.FirebaseCredentialsManager
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.provideAuthService
import co.orangesoft.authmanager.api.provideOkHttp
import co.orangesoft.authmanager.api.provideProfileService
import co.orangesoft.authmanager.user.SVBaseUserController
import okhttp3.Interceptor

class SVAuthManager private constructor(credManager: SVCredentialManager): AuthManager<SVBaseUserController, FirebaseCredentialsManager.FirebaseCredential>(credManager as CredentialsManager<SVBaseUserController, FirebaseCredentialsManager.FirebaseCredential>) {

    enum class UserStatus {
        REGISTERED,
        UNREGISTERED
    }

    companion object {
        fun getInstance(context: Context): SVAuthManager {
            return SVAuthManager(
                SVCredentialManager(
                    AccountManager.get(context),
                    provideAuthService("http://github.com", provideOkHttp(arrayListOf())),
                    provideProfileService("http://github.com", provideOkHttp(arrayListOf())),
                    "co.orangesoft.authmanager", ""
                )
            )
        }
    }

    val userStatus: LiveData<UserStatus> by lazy {
        MutableLiveData<UserStatus>().apply { postValue(
            UserStatus.UNREGISTERED
        ) }
    }


    init {
        currentUser.value
        userCredentials.observeForever { creds ->
            if(creds.isEmpty() || (creds.size == 1 && creds.first().providerId == "firebase"))
                (userStatus as MutableLiveData).postValue(UserStatus.UNREGISTERED)
            else
                (userStatus as MutableLiveData).postValue(UserStatus.REGISTERED)
        }
    }
}