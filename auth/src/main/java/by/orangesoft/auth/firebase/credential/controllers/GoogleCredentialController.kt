package by.orangesoft.auth.firebase.credential.controllers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.IBaseCredentialController
import by.orangesoft.auth.credentials.CredentialListener
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.firebase.credential.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class GoogleCredentialController(method: Firebase.Google): IBaseCredentialController {

    override val credential: AuthCredential = method

    private lateinit var activityCallback: Task<AuthResult>

    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private fun googleSingInClient(activity: FragmentActivity): GoogleSignInClient {
        val oprions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((credential as Firebase.Google).clientId)
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(activity, oprions)
    }

    private var addCredListener: CredentialListener? = null

    override fun addCredential(listener: CredentialListener) {

        authInstance.currentUser?.let { user ->
            user.providerData.firstOrNull { it.providerId == credential.providerId }?.let {
                user.getIdToken(true)
                    .addOnSuccessListener { listener(CredentialResult(credential, it.token ?: "")) }
                    .addOnFailureListener {
                        authInstance.signOut()
                        addCredential(listener)
                    }
                return
            }
        }

        if (!::activityCallback.isInitialized) {
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
                        .addOnSuccessListener { listener(CredentialResult(credential, it.token ?: "")) }
                        .addOnFailureListener { listener(it) }
                } ?: listener(KotlinNullPointerException("Firebase user is NULL"))
            }

        addCredListener = null
    }

    override fun removeCredential(listener: CredentialListener) {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == credential.providerId
        }?.let { provider ->
            authInstance.currentUser?.unlink(provider.providerId)
                ?.addOnSuccessListener { listener(credential) }
                ?.addOnFailureListener { listener(it) }
        } ?: listener(credential)
    }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {
        activityLauncher.launch(googleSingInClient(activity).signInIntent)
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