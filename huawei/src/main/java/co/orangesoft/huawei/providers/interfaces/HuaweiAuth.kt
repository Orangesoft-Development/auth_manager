package co.orangesoft.huawei.providers.interfaces

import co.orangesoft.huawei.credential.HuaweiAuthCredential

interface HuaweiAuth {

    fun requestSecurityCode(credential: HuaweiAuthCredential)

    fun registerUser(credential: HuaweiAuthCredential)

    fun signIn(credential: HuaweiAuthCredential)

    fun signOutUser()

    fun deleteUser()

    fun signInAnonymously()

    fun resetPassword(credential: HuaweiAuthCredential)
}