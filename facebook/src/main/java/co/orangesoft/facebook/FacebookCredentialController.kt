package co.orangesoft.facebook

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.LoginStatusCallback
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.*
import kotlinx.coroutines.CancellationException
import java.lang.Exception

/**
 * Credential controller using FirebaseAuth with facebook provider
 *
 * @see FirebaseAuthCredential.Facebook
 * @see FirebaseAuth
 *
 */

class FacebookCredentialController :
    BaseFirebaseCredentialController(FirebaseAuthCredential.Facebook) {

    private val permissions = listOf("email", "public_profile")

    private val callbackFactory: CallbackManager by lazy { CallbackManager.Factory.create() }

    private val loginManager: LoginManager by lazy {
        LoginManager.getInstance().apply {
            registerCallback(callbackFactory, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    onSuccessLogin(result.accessToken.token)
                }

                override fun onCancel() {
                    onError(CancellationException("Error add credential ${authCredential.providerId} cancelled by user"))
                }

                override fun onError(error: FacebookException) {
                    onError("Error add credential ${authCredential.providerId}", error)
                }
            })

        }
    }

    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {
        if (isAutoLoginSupport()) {
            retrieveLoginStatus(activity)
        } else {
            logIn(activity)
        }
    }

    override fun onActivityResult(code: Int, data: Intent?) {
        callbackFactory.onActivityResult(
            CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode(),
            code,
            data
        )
    }

    override fun clearCredInfo(context: Context) {
        super.clearCredInfo(context)
        loginManager.logOut()
    }

    private fun logIn(activity: FragmentActivity) =
        loginManager.setLoginBehavior(LoginBehavior.WEB_ONLY).logIn(activity, permissions)

    private fun retrieveLoginStatus(activity: FragmentActivity) {
        loginManager.retrieveLoginStatus(activity, object : LoginStatusCallback {

            override fun onCompleted(accessToken: AccessToken) {
                onSuccessLogin(accessToken.token)
            }

            override fun onFailure() {
                Log.i("!!!", "${authCredential.providerId} autoLogin failed, try simple logIn")
                logIn(activity)
            }

            override fun onError(exception: Exception) {
                onError("Error add credential ${authCredential.providerId}", exception)
            }
        })
    }

    private fun onSuccessLogin(authToken: String) =
        emitAuthTask(FacebookAuthProvider.getCredential(authToken))

    private fun isAutoLoginSupport(): Boolean =
        try {
            FacebookSdk.getApplicationContext().packageManager.getApplicationInfo(
                "com.facebook.katana",
                0
            )
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

}