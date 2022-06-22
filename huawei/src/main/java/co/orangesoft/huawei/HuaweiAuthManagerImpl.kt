package co.orangesoft.huawei

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import com.huawei.agconnect.auth.*
import java.util.*

class HuaweiAuthManagerImpl {

    private val agConnectAuth: AGConnectAuth = AGConnectAuth.getInstance()

    enum class UserStatus {
        UNREGISTERED,
        REGISTERED
    }


    fun requestEmailCode(credential: HuaweiAuthCredential.Email) {
        val task = agConnectAuth.requestVerifyCode(credential.email, getSettings())
        task.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun requestPhoneCode(credential: HuaweiAuthCredential.Phone) {
        val task = agConnectAuth.requestVerifyCode(
            credential.countryCode,
            credential.phoneNumber,
            getSettings()
        )
        task.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun registerByEmail(credential: HuaweiAuthCredential.Email) {
        val emailUser = EmailUser.Builder()
            .setEmail(credential.email)
            .setVerifyCode(credential.securityCode)
            .setPassword(credential.password)
            .build()
        agConnectAuth.createUser(emailUser).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun registerByPhone(credential: HuaweiAuthCredential.Phone) {
        val phoneUser = PhoneUser.Builder()
            .setCountryCode(credential.countryCode)
            .setPhoneNumber(credential.phoneNumber)
            .setVerifyCode(credential.securityCode)
            .setPassword(credential.password)
            .build()
        agConnectAuth.createUser(phoneUser).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun signInByEmailPassword(credential: HuaweiAuthCredential.Email) {
        val emailCredential =
            EmailAuthProvider.credentialWithPassword(credential.email, credential.password)
        agConnectAuth.signIn(emailCredential).addOnSuccessListener {
        }.addOnFailureListener {

        }
    }

    fun signInByPhonePassword(
        credential: HuaweiAuthCredential.Phone
    ) {
        val phoneCredential = PhoneAuthProvider.credentialWithPassword(
            credential.countryCode,
            credential.phoneNumber,
            credential.password
        )
        agConnectAuth.signIn(phoneCredential).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun signInByEmailCode(
        credential: HuaweiAuthCredential.Email
    ) {
        val emailCredential = EmailAuthProvider.credentialWithVerifyCode(
            credential.email,
            credential.password,
            credential.securityCode
        )
        agConnectAuth.signIn(emailCredential).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun signInUserByPhoneCode(
        credential: HuaweiAuthCredential.Phone
    ) {
        val phoneCredential =
            PhoneAuthProvider.credentialWithVerifyCode(
                credential.countryCode,
                credential.phoneNumber,
                credential.password,
                credential.securityCode
            )
        agConnectAuth.signIn(phoneCredential).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }


    fun deleteUser() {
        agConnectAuth.deleteUser().addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun signOutUser() {
        agConnectAuth.signOut()
    }

    private fun getSettings(): VerifyCodeSettings {
        return VerifyCodeSettings.newBuilder()
            .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(30)
            .locale(Locale.ENGLISH)
            .build()
    }

}