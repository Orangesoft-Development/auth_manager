package co.orangesoft.phone

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class PhoneCredentialController(method: Firebase.Phone): BaseFirebaseCredentialController(method) {

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
                    Log.e("!!!", "onCodeSent $verificationId")
                    credential.onCodeSentListener?.invoke(verificationId)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.e("!!!", credential.smsCode)
                    activityCallback = getAuthTask(credential)
                    getCredential()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.e("!!!", p0.message!!)
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    Log.e("!!!", "onCodeAutoRetrievalTimeOut $p0")
                    super.onCodeAutoRetrievalTimeOut(p0)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {
        Log.e("!!!", "clientId: ${(credential as Firebase.Phone).phoneNumber}")
        phoneSingInClient(activity)
    }

    override fun onActivityResult(code: Int, data: Intent?) {}
}