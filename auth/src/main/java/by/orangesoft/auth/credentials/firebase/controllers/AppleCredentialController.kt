package by.orangesoft.auth.credentials.firebase.controllers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseCredentialController
import by.orangesoft.auth.credentials.CredentialListener
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.Firebase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import java.lang.RuntimeException
import java.util.*

class AppleCredentialController: BaseCredentialController(Firebase.Apple) {

    private lateinit var activityCallback: Task<AuthResult>

    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val appleSingInClient: OAuthProvider by lazy {
        OAuthProvider.newBuilder(method.providerId).apply {
            addCustomParameter("locale", Locale.getDefault().language)
            scopes = listOf("email", "name")
        }.build()
    }

    override fun addCredential(listener: CredentialListener) {
        authInstance.currentUser?.let { user ->
            user.providerData.firstOrNull { it.providerId == method.providerId }?.let {
                user.getIdToken(true)
                    .addOnSuccessListener { listener(CredentialResult(method, it.token ?: "")) }
                    .addOnFailureListener {
                        authInstance.signOut()
                        addCredential(listener)
                    }
                return
            }
        }

        if(!this@AppleCredentialController::activityCallback.isInitialized) {
            listener(RuntimeException("Firebase ${method.providerId} provider is not create"))
            return
        }

        activityCallback
            .addOnSuccessListener { result ->
                val user = result.user

                if (user == null) {
                    listener(KotlinNullPointerException("Firebase user is NULL"))
                    return@addOnSuccessListener

                } else {
                    user.getIdToken(true)
                        .addOnSuccessListener { listener(CredentialResult(method, it.token ?: "")) }
                        .addOnFailureListener { listener(it) }
                }
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
        activityCallback = authInstance.currentUser?.let { currentUser ->
            if(!currentUser.isAnonymous && currentUser.providerData.size > 1)
                currentUser.startActivityForLinkWithProvider(activity, appleSingInClient)
            else null
        } ?: authInstance.startActivityForSignInWithProvider(activity, appleSingInClient)
    }

    override fun onActivityResult(code: Int, data: Intent?) {}
}