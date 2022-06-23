package co.orangesoft.huawei.interfaces

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import com.huawei.agconnect.auth.AGConnectUser

interface IHuaweiAuthManager {

    fun requestSecurityCode(credential: HuaweiAuthCredential)

    fun registerUser(credential: HuaweiAuthCredential)

    fun signIn(credential: HuaweiAuthCredential)

    fun signOutUser()

    fun deleteUser()

    fun getCurrentUser(): AGConnectUser

    fun signInAnonymously()

    fun resetPassword(credential: HuaweiAuthCredential)
}