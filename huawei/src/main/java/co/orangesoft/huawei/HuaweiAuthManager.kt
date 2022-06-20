package co.orangesoft.huawei

import com.huawei.agconnect.auth.*
import java.util.*

class HuaweiAuthManager {

    private val agConnectAuth: AGConnectAuth = AGConnectAuth.getInstance()

    enum class UserStatus {
        UNREGISTERED,
        REGISTERED
    }


    fun requestEmailCode(email: String) {
        val task = agConnectAuth.requestVerifyCode(email, getSettings())
        task.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun requestPhoneCode(countryCode: String, phone: String) {
        val task = agConnectAuth.requestVerifyCode(countryCode, phone, getSettings())
        task.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun registerByEmail(email: String, code: String, password: String) {
        val emailUser = EmailUser.Builder()
            .setEmail(email)
            .setVerifyCode(code)
            .setPassword(password)
            .build()
        agConnectAuth.createUser(emailUser).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun registerByPhone(
        countryCode: String,
        phone: String,
        code: String,
        password: String
    ) {

        val phoneUser = PhoneUser.Builder()
            .setCountryCode(countryCode)
            .setPhoneNumber(phone)
            .setVerifyCode(code)
            .setPassword(password)
            .build()
        agConnectAuth.createUser(phoneUser).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun signInByEmailPassword(email: String, password: String) {
        val credential = EmailAuthProvider.credentialWithPassword(email, password)
        agConnectAuth.signIn(credential).addOnSuccessListener {
        }.addOnFailureListener {

        }
    }

    fun signInByPhonePassword(countryCode: String, phone: String, password: String) {
        val credential = PhoneAuthProvider.credentialWithPassword(countryCode, phone, password)
        agConnectAuth.signIn(credential).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun signInByEmailCode(email: String, code: String, password: String) {
        val credential = EmailAuthProvider.credentialWithVerifyCode(email, password, code)
        agConnectAuth.signIn(credential).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun signInUserByPhoneCode(
        countryCode: String,
        phone: String,
        code: String,
        password: String
    ) {
        val credential =
            PhoneAuthProvider.credentialWithVerifyCode(countryCode, phone, password, code)
        agConnectAuth.signIn(credential).addOnSuccessListener {

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