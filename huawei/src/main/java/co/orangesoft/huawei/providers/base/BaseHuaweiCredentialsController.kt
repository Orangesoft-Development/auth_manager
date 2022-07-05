package co.orangesoft.huawei.providers.base

import co.orangesoft.huawei.credential.HuaweiCredentialResult
import co.orangesoft.huawei.providers.phone.IHuaweiAuthPhone
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.VerifyCodeSettings
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

abstract class BaseHuaweiCredentialsController : IHuaweiAuthPhone {

    val agConnectAuth: AGConnectAuth = AGConnectAuth.getInstance()


    fun getSettings(): VerifyCodeSettings {
        return VerifyCodeSettings.newBuilder()
            .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(30)
            .locale(Locale.ENGLISH)
            .build()
    }

    override fun signOutUser() {
        agConnectAuth.signOut()
    }

    override fun deleteUser() {
        agConnectAuth.deleteUser()
    }

    override fun getCurrentUser() =
        if (agConnectAuth.currentUser == null)
            null
        else
            HuaweiCredentialResult(
                anonymous = agConnectAuth.currentUser.isAnonymous,
                uid = agConnectAuth.currentUser.uid,
                email = agConnectAuth.currentUser.email,
                phone = agConnectAuth.currentUser.phone,
                displayName = agConnectAuth.currentUser.displayName,
                photoUrl = agConnectAuth.currentUser.photoUrl
            )

}