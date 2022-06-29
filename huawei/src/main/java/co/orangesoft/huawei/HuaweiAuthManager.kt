package co.orangesoft.huawei

import android.util.Log
import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.credential.HuaweiCredentialResult
import co.orangesoft.huawei.interfaces.IHuaweiAuthManager
import co.orangesoft.huawei.providers.HuaweiAuthProvider
import co.orangesoft.huawei.providers.email.HuaweiEmailCredentialsController
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth
import co.orangesoft.huawei.providers.phone.HuaweiPhoneCredentialsController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

object HuaweiAuthManager : IHuaweiAuthManager, CoroutineScope {

    private var huaweiAuth: HuaweiAuth by Delegates.notNull()

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Default + SupervisorJob() }

    enum class UserStatus {
        UNREGISTERED,
        REGISTERED
    }

    private val _user: MutableStateFlow<HuaweiCredentialResult> by lazy {MutableStateFlow(HuaweiCredentialResult())}
    val user: MutableStateFlow<HuaweiCredentialResult> by lazy { _user }


    private val _status: MutableStateFlow<UserStatus> by lazy { MutableStateFlow(UserStatus.UNREGISTERED) }
    val userStatus: StateFlow<UserStatus> by lazy { _status.asStateFlow() }


    fun initAuthManager(provider: HuaweiAuthProvider) {

        huaweiAuth = when (provider) {
            HuaweiAuthProvider.PHONE -> HuaweiPhoneCredentialsController.getInstance()
            HuaweiAuthProvider.EMAIL -> HuaweiEmailCredentialsController.getInstance()
        }

        val user = huaweiAuth.getCurrentUser()
        Log.d("TAG", "$user")
    }

    override fun requestSecurityCode(credential: HuaweiAuthCredential) {
        huaweiAuth.requestSecurityCode(credential)
    }

    override fun registerUser(credential: HuaweiAuthCredential) {
        huaweiAuth.registerUser(credential)
    }

    override fun signIn(credential: HuaweiAuthCredential) {
        huaweiAuth.signIn(credential)
    }

    override fun signOutUser() {
        huaweiAuth.signOutUser()
    }

    override fun deleteUser() {
        huaweiAuth.deleteUser()
    }

    override fun getCurrentUser() = huaweiAuth.getCurrentUser()

    override fun changeEmail(newEmail: String, newVerifyCode: String) {
        huaweiAuth.changeEmail(newEmail, newVerifyCode)
    }

    override fun changePhone(newCountryCode: String, newPhone: String, newVerifyCode: String) {
        huaweiAuth.changePhone(newCountryCode, newPhone, newVerifyCode)
    }

    override fun changePassword(newPassword: String, newVerifyCode: String) {
        huaweiAuth.changePassword(newPassword, newVerifyCode)
    }

    override fun resetPassword(credential: HuaweiAuthCredential, newPassword: String, newVerifyCode: String) {
        huaweiAuth.resetPassword(credential, newPassword, newVerifyCode)
    }

    private fun Flow<HuaweiCredentialResult>.takeSingleUserFlow() = onEach { user.value = it }.take(1)
    private fun Flow<HuaweiCredentialResult>.launchInWithCatch() = catch { it.printStackTrace() }.launchIn(this@HuaweiAuthManager)
}