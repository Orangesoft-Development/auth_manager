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

class HuaweiAuthManager : IHuaweiAuthManager, CoroutineScope {



    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Default + SupervisorJob() }

    companion object {

        private var huaweiAuth: HuaweiAuth by Delegates.notNull()

        private val _user: MutableStateFlow<HuaweiCredentialResult> by lazy {
            MutableStateFlow(HuaweiCredentialResult())
        }

        fun getInstance(provider: HuaweiAuthProvider): HuaweiAuthManager {
            initAuthManager(provider)
            return HuaweiAuthManager()
        }

        private fun initAuthManager(provider: HuaweiAuthProvider) {

            huaweiAuth = when (provider) {
                HuaweiAuthProvider.PHONE -> HuaweiPhoneCredentialsController.getInstance()
                HuaweiAuthProvider.EMAIL -> HuaweiEmailCredentialsController.getInstance()
            }

            val user = huaweiAuth.getCurrentUser()
            Log.d("TAG", "$user")
        }
    }


    enum class UserStatus {
        UNREGISTERED,
        REGISTERED
    }

    val user: MutableStateFlow<HuaweiCredentialResult> by lazy { _user }

    val currentUser: StateFlow<HuaweiCredentialResult> by lazy { user.asStateFlow() }

    private val _status: MutableStateFlow<UserStatus> by lazy { MutableStateFlow(UserStatus.UNREGISTERED) }
    val userStatus: StateFlow<UserStatus> by lazy { _status.asStateFlow() }



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