package co.orangesoft.huawei.providers.email

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth
import com.huawei.agconnect.auth.*
import java.util.*

class HuaweiEmailCredentialsController() :
    IHuaweiAuthEmail {

    private val agConnectAuth: AGConnectAuth = AGConnectAuth.getInstance()

    companion object {

        @JvmStatic
        fun getInstance(): HuaweiAuth = HuaweiEmailCredentialsController()

    }

    private fun getSettings(): VerifyCodeSettings {
        return VerifyCodeSettings.newBuilder()
            .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(30)
            .locale(Locale.ENGLISH)
            .build()
    }


    override fun requestSecurityCode(credential: HuaweiAuthCredential) {
        val task = agConnectAuth.requestVerifyCode(credential.email, getSettings())
        task.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    override fun registerUser(credential: HuaweiAuthCredential) {
        val emailUser = EmailUser.Builder()
            .setEmail(credential.email)
            .setVerifyCode(credential.securityCode)
            .setPassword(credential.password)
            .build()
        agConnectAuth.createUser(emailUser).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    override fun signIn(credential: HuaweiAuthCredential) {
        val emailCredential =
            EmailAuthProvider.credentialWithPassword(credential.email, credential.password)
        agConnectAuth.signIn(emailCredential).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    override fun signOutUser() {
        agConnectAuth.signOut()
    }

    override fun deleteUser() {
        agConnectAuth.deleteUser()
    }

    override fun getCurrentUser(): AGConnectUser {
        return agConnectAuth.currentUser
    }

    override fun changeEmail(newEmail: String) {
        TODO("Not yet implemented")
    }

    override fun changePhone(newPhone: String) {
        TODO("Not yet implemented")
    }

    override fun changePassword(newPassword: String) {
        TODO("Not yet implemented")
    }

    override fun resetPassword(credential: HuaweiAuthCredential) {
        //required newPassword, verifyCode
        agConnectAuth.currentUser.updatePassword(credential.password, credential.securityCode ,AGConnectAuthCredential.Email_Provider)
            .addOnSuccessListener {
                // onSuccess
            }.addOnFailureListener {
                // onFail
            }
    }

/*


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
*/


}