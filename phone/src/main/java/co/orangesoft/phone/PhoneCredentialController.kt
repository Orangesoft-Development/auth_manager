package co.orangesoft.phone

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class PhoneCredentialController(private val method: Firebase.Phone) :
    BaseFirebaseCredentialController(method) {

    private fun phoneSingInClient(activity: FragmentActivity) {
        val options = PhoneAuthOptions.newBuilder(authInstance)
            .setPhoneNumber((credential as Firebase.Phone).phoneNumber)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.i("!!!", "Code sent $verificationId")
                    credential.onCodeSentListener?.invoke(verificationId, forceResendingToken)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.i("!!!", "Verification completed: ${credential.smsCode}")
                    onAuthCompleted(credential)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.e("!!!", "Verification failed: ${p0.message}")
                    onError("Error phoneSingIn", p0)
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    Log.e("!!!", "CodeAutoRetrievalTimeOut $p0")
                    super.onCodeAutoRetrievalTimeOut(p0)
                }
            })
        (method.forceResendingToken as? PhoneAuthProvider.ForceResendingToken)?.let {
            options.setForceResendingToken(it)
        }
        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {
        Log.i("!!!", "ClientId: ${(credential as Firebase.Phone).phoneNumber}")
        if (method.verificationId == null) {
            Log.i("!!!", "Create phone provider")
            phoneSingInClient(activity)
        }
    }

    override fun updateCurrentCredential(user: FirebaseUser, authCredential: AuthCredential) {
        user.updatePhoneNumber(authCredential as PhoneAuthCredential)
            .addOnFailureListener { onError("Error update current credential", it) }
            .addOnSuccessListener {
                user.getIdToken(false)
                    .addOnSuccessListener { flow.tryEmit(CredentialResult(credential, it.token!!)) }
                    .addOnFailureListener { onError("Error update current credential", it) }
            }
    }

    override fun onActivityResult(code: Int, data: Intent?) {}

    override fun getCredential() {
        if (!isActivityCallbackInitialised()) {
            if (method.verificationId != null && method.code != null) {
                val credential = PhoneAuthProvider.getCredential(method.verificationId!!, method.code!!)
                onAuthCompleted(credential)
            }
            return
        }
        super.getCredential()
    }

    private fun onAuthCompleted(credential: PhoneAuthCredential) {
        if (!isActivityCallbackInitialised()) {
            emitAuthTask(credential)
        }
    }

}