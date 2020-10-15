package by.orangesoft.auth.credentials.firebase.controllers

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseCredentialController
import by.orangesoft.auth.credentials.CredentialListener
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.lang.RuntimeException

class GoogleCredentialController(method: Firebase.Google): BaseCredentialController(method) {

    private lateinit var activityCallback: Task<AuthResult>

    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private fun googleSingInClient(activity: FragmentActivity): GoogleSignInClient {
        val oprions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((method as Firebase.Google).clientId)
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(activity, oprions)
    }

    private var addCredListener: CredentialListener? = null

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

        if (!::activityCallback.isInitialized) {
            Log.e("GoogleCredentialController", "suspend call")
            addCredListener = listener
            return
        }
        addCredProcess(listener)
    }

    private fun addCredProcess(listener: CredentialListener) {
        activityCallback.addOnFailureListener { listener(it) }
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    user.getIdToken(true)
                        .addOnSuccessListener { listener(CredentialResult(method, it.token!!)) }
                        .addOnFailureListener { listener(it) }
                } ?: listener(RuntimeException("Firebase user is NULL"))
            }

        addCredListener = null
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
        activityLauncher?.launch(googleSingInClient(activity).signInIntent)
    }

    override fun onActivityResult(code: Int, data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data).apply {
            addOnSuccessListener { account ->
                activityCallback = authInstance.currentUser?.let { currentUser ->
                    if(!currentUser.isAnonymous && currentUser.providerData.size > 1)
                        currentUser.linkWithCredential(GoogleAuthProvider.getCredential(account.idToken, null))
                    else null
                } ?: authInstance.signInWithCredential(GoogleAuthProvider.getCredential(account.idToken, null))

                addCredListener?.apply { addCredProcess(this) }
            }
            addOnFailureListener {
                addCredListener?.invoke(it)
                addCredListener = null
            }
        }
    }
}