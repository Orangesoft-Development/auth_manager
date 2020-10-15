package by.orangesoft.auth.credentials.firebase.controllers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseCredentialController
import by.orangesoft.auth.credentials.CredentialListener
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.Firebase
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.lang.RuntimeException

class FacebookCredentialController: BaseCredentialController(Firebase.Facebook) {

    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var activityCallback: Task<AuthResult>

    private val callbackFactory: CallbackManager by lazy { CallbackManager.Factory.create() }

    private var addCredListener: CredentialListener? = null

    private val loginManager: LoginManager by lazy {
        LoginManager.getInstance().apply {
            registerCallback(callbackFactory, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    activityCallback =
                        authInstance.currentUser?.let { currentUser ->
                            if(!currentUser.isAnonymous && currentUser.providerData.size > 1)
                                currentUser.linkWithCredential(FacebookAuthProvider.getCredential(result.accessToken.token))
                            else null
                        } ?: authInstance.signInWithCredential(FacebookAuthProvider.getCredential(result.accessToken.token))

                    addCredListener?.apply { addCredential(this) }
                }

                override fun onCancel() { addCredListener?.invoke(RuntimeException("Login by $method is cancelled")) }

                override fun onError(error: FacebookException) { addCredListener?.invoke(error) }
            })
        }
    }

    override fun addCredential(listener: CredentialListener) {

        authInstance.currentUser?.let { user ->
            user.providerData.firstOrNull { it.providerId == method.providerId }?.let {
                user.getIdToken(true)
                    .addOnSuccessListener { listener(CredentialResult(method, it.token!!)) }
                    .addOnFailureListener {
                        authInstance.signOut()
                        addCredential(listener)
                    }
                return
            }
        }

        if(!::activityCallback.isInitialized) {
            addCredListener = listener
            return
        }

        activityCallback
            .addOnSuccessListener { result ->
                if(result.user == null) {
                    listener(RuntimeException("Firebase user is NULL"))
                    return@addOnSuccessListener
                }

                result.user!!.getIdToken(true)
                    .addOnSuccessListener { listener(CredentialResult(method, it.token!!)) }
                    .addOnFailureListener { listener(it) }
            }
            .addOnFailureListener { listener(it) }
    }


    override fun removeCredential(listener: CredentialListener) {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == method.providerId
        }?.let { provider ->
            authInstance.currentUser?.unlink(provider.providerId)
                ?.addOnSuccessListener { listener(method) }
                ?.addOnFailureListener { listener(it) }
        } ?: listener(method)
    }

    override fun createProvider(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {
        loginManager.logIn(activity, listOf("email", "public_profile"))
    }

    override fun onActivityResult(code: Int, data: Intent?) {
        callbackFactory.onActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode(), code, data)
    }
}