package co.orangesoft.huawei.providers.phone

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.providers.base.BaseHuaweiCredentialsController
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth
import com.huawei.agconnect.auth.AGConnectAuthCredential
import com.huawei.agconnect.auth.PhoneAuthProvider
import com.huawei.agconnect.auth.PhoneUser

internal class HuaweiPhoneCredentialsController : BaseHuaweiCredentialsController() {

    companion object {

        @JvmStatic
        fun getInstance(): HuaweiAuth = HuaweiPhoneCredentialsController()

    }

    override fun requestSecurityCode(credential: HuaweiAuthCredential) {
        val task = agConnectAuth.requestVerifyCode(
            credential.countryCode,
            credential.phoneNumber,
            getSettings()
        )
        task.addOnSuccessListener {
            // onSuccess
        }.addOnFailureListener {
            // onFail
        }
    }

    override fun registerUser(credential: HuaweiAuthCredential) {
        val phoneUser = PhoneUser.Builder()
            .setCountryCode(credential.countryCode)
            .setPhoneNumber(credential.phoneNumber)
            .setVerifyCode(credential.securityCode)
            .setPassword(credential.password)
            .build()
        agConnectAuth.createUser(phoneUser).addOnSuccessListener {
            // A newly created user account is automatically signed in to your app.
        }.addOnFailureListener {
            // onFail
        }
    }

    override fun signIn(credential: HuaweiAuthCredential) {
        val phoneCredential = PhoneAuthProvider.credentialWithPassword(
            credential.countryCode,
            credential.phoneNumber,
            credential.password
        )
        agConnectAuth.signIn(phoneCredential).addOnSuccessListener {
            // Obtain sign-in information.
        }.addOnFailureListener {
            // onFail
        }
    }

    override fun changeEmail(newEmail: String, newVerifyCode: String) {}

    override fun changePhone(newCountryCode: String, newPhone: String, newVerifyCode: String) {

        agConnectAuth.currentUser.updatePhone(newCountryCode, newPhone, newVerifyCode)
            .addOnSuccessListener {
                // onSuccess
            }.addOnFailureListener {
                // onFail
            }
    }

    override fun changePassword(newPassword: String, newVerifyCode: String) {
        agConnectAuth.currentUser.updatePassword(newPassword, newVerifyCode, AGConnectAuthCredential.Phone_Provider)
            .addOnSuccessListener {
                // onSuccess
            }.addOnFailureListener {
                // onFail
            }
    }

    override fun resetPassword(
        credential: HuaweiAuthCredential,
        newPassword: String,
        verifyCode: String
    ) {
        agConnectAuth.resetPassword(credential.countryCode, credential.phoneNumber, newPassword, verifyCode)
            .addOnSuccessListener {
                // onSuccess
            }.addOnFailureListener {
                // onFail
            }
    }

}