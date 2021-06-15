package co.orangesoft.facebook

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.*
import kotlin.coroutines.cancellation.CancellationException

class FacebookCredentialController: BaseFirebaseCredentialController(FirebaseAuthCredential.Facebook) {

    private val callbackFactory: CallbackManager by lazy { CallbackManager.Factory.create() }

    private val loginManager: LoginManager by lazy {
        LoginManager.getInstance().apply {
            registerCallback(callbackFactory, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    activityCallback = getAuthTask(FacebookAuthProvider.getCredential(result.accessToken.token))
                    getCredential()
                }

                override fun onCancel() { onError(CancellationException("Error add credential ${authCredential.providerId} cancelled by user")) }

                override fun onError(error: FacebookException) { onError("Error add credential ${authCredential.providerId}", error) }
            })
        }
    }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {
        loginManager.logIn(activity, listOf("email", "public_profile"))
    }

    override fun onActivityResult(code: Int, data: Intent?) {
        callbackFactory.onActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode(), code, data)
    }
}