package co.orangesoft.phone

import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.UpdateCredAuthResult
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class PhoneCredentialController(private val phoneAuthCredential: FirebaseAuthCredential.Phone) :
    BaseFirebaseCredentialController(phoneAuthCredential) {

    companion object {
        private val phoneCredResendingToken = PhoneCredResendingToken()
    }

    private fun phoneSingInClient(activity: FragmentActivity) {
        authInstance.setLanguageCode(Locale.getDefault().language)
        val options = PhoneAuthOptions.newBuilder(authInstance)
            .setPhoneNumber(phoneAuthCredential.phoneNumber)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.i("!!!", "Code sent $verificationId")
                    phoneCredResendingToken.forceResendingToken = forceResendingToken
                    phoneAuthCredential.onCodeSentListener?.invoke(verificationId)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.i("!!!", "Verification completed: ${credential.smsCode}")
                    phoneCredResendingToken.clear()
                    emitAuthTask(credential)
                    launch { getCredential(currentCoroutineContext()) }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.e("!!!", "Verification failed: ${p0.message}")
                    onError("Error phoneSingIn", p0)
                    phoneCredResendingToken.clear()
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    Log.e("!!!", "CodeAutoRetrievalTimeOut $p0")
                    super.onCodeAutoRetrievalTimeOut(p0)
                }
            })

        (phoneCredResendingToken.updatePhoneCred(phoneAuthCredential.phoneNumber)
            .forceResendingToken as? PhoneAuthProvider.ForceResendingToken)?.let {
            Log.i("!!!", "Phone provider: resend sms")
            options.setForceResendingToken(it)
        }
        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {
        Log.i("!!!", "ClientId: ${phoneAuthCredential.phoneNumber}")
        if (phoneAuthCredential.verificationId == null) {
            Log.i("!!!", "Create phone provider")
            phoneSingInClient(activity)
        }
    }

    override fun updateCurrentCredential(
        user: FirebaseUser,
        authCredential: AuthCredential
    ): Task<UpdateCredAuthResult> =
        user.updatePhoneNumber(authCredential as PhoneAuthCredential)
            .continueWithTask {
                it.exception?.let { throw it } ?: run {
                    val completionSource = TaskCompletionSource<UpdateCredAuthResult>().also {
                        it.setResult(UpdateCredAuthResult(user, authCredential))
                    }
                    completionSource.task
                }
            }

    override fun onActivityResult(code: Int, data: Intent?) {}

    override suspend fun getCredential(coroutineContext: CoroutineContext) {
        if (phoneAuthCredential.verificationId != null && phoneAuthCredential.code != null) {
            val credential = PhoneAuthProvider.getCredential(
                phoneAuthCredential.verificationId!!,
                phoneAuthCredential.code!!
            )
            emitAuthTask(credential)
        }
        super.getCredential(coroutineContext)
    }

}

private data class PhoneCredResendingToken(
    var phoneNumber: String? = null,
    var forceResendingToken: Parcelable? = null
) {

    fun updatePhoneCred(newPhoneNumber: String) = apply {
        if (phoneNumber != newPhoneNumber) {
            phoneNumber = newPhoneNumber
            forceResendingToken = null
        }
    }

    fun clear() {
        phoneNumber = null
        forceResendingToken = null
    }

}