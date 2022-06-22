package co.orangesoft.huawei.interfaces

import co.orangesoft.huawei.credential.HuaweiAuthCredential

interface IHuaweiAuthManager {

    fun requestSecurityCode(credential: HuaweiAuthCredential)

    fun registerUser(credential: HuaweiAuthCredential)

    fun signIn(credential: HuaweiAuthCredential)

    fun signOutUser()

    fun deleteUser()

    fun signInAnonymously()

    fun resetPassword(credential: HuaweiAuthCredential)
}