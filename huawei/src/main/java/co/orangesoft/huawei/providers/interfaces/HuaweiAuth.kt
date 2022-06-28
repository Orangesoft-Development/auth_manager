package co.orangesoft.huawei.providers.interfaces

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.credential.HuaweiCredentialResult

interface HuaweiAuth {

    fun requestSecurityCode(credential: HuaweiAuthCredential)

    fun registerUser(credential: HuaweiAuthCredential)

    fun signIn(credential: HuaweiAuthCredential)

    fun signOutUser()

    fun deleteUser()

    fun getCurrentUser(): HuaweiCredentialResult

    fun changeEmail(newEmail: String, newVerifyCode: String)

    fun changePhone(newCountryCode: String, newPhone: String, newVerifyCode: String)

    fun changePassword(newPassword: String, newVerifyCode: String)

    fun resetPassword(credential: HuaweiAuthCredential, newPassword: String, verifyCode: String)
}