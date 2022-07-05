package co.orangesoft.authmanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import co.orangesoft.authmanager.auth.SimpleAuthManager
import co.orangesoft.authmanager.auth.email.EmailAuthCredential
import co.orangesoft.authmanager.auth.phone.SimplePhoneAuthCredential
import co.orangesoft.authmanager.databinding.ActivityMainBinding
import co.orangesoft.authmanager.firebase_auth.AuthManager
import co.orangesoft.huawei.HuaweiAuthManager
import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.providers.HuaweiAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@InternalCoroutinesApi
class MainActivity : FragmentActivity() {

    private val authManager: AuthManager by lazy {
        AuthManager.getInstance(applicationContext, AuthManager.BASE_URL)
    }
    private val simpleAuthManager: SimpleAuthManager by lazy {
        SimpleAuthManager.getInstance(SimpleAuthManager.BASE_URL, applicationContext)
    }

    private val huaweiAuthManager : HuaweiAuthManager by lazy {
        HuaweiAuthManager.getInstance(HuaweiAuthProvider.PHONE)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        signInAnonymously()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        binding.apply {
            googleBtn.setOnClickListener {
                launchCredential(
                    FirebaseAuthCredential.Google(getString(R.string.server_client_id))
                )
            }

            facebookBtn.setOnClickListener { launchCredential(FirebaseAuthCredential.Facebook) }

            appleBtn.setOnClickListener { launchCredential(FirebaseAuthCredential.Apple) }

            phoneBtn.setOnClickListener {
                launchCredential(FirebaseAuthCredential.Phone("+375334445566") { verificationId ->
                    // manually launchCredential with code + verificationId
                    launchCredential(
                        FirebaseAuthCredential.Phone(
                            "+375334445566",
                            "123456",
                            verificationId
                        )
                    )
                })
            }

            simplePhoneBtn.setOnClickListener {
                launchSimpleCredential(SimplePhoneAuthCredential("+375334445566", "1234"))
            }

            simpleEmailBtn.setOnClickListener {
                launchSimpleCredential(EmailAuthCredential("email@gmail.com", "password111"))
            }

            simpleRemoveEmailBtn.setOnClickListener {
                launchSimpleCredential(EmailAuthCredential("email@gmail.com", "password111"), true)
            }

            simpleRemovePhoneBtn.setOnClickListener {
                launchSimpleCredential(SimplePhoneAuthCredential("+375334445566", "1234"), true)
            }

            logout.setOnClickListener {
                if (authManager.userStatus.value == AuthManager.UserStatus.REGISTERED)
                    credentialLogout()
            }

            huaweiRequestSecurityCodeBtn.setOnClickListener {

            }

            huaweiRegisterUserBtn.setOnClickListener {

            }

            huaweiSignInBtn.setOnClickListener {

            }

            hiaweiSignOutBtn.setOnClickListener {
                if (huaweiAuthManager.userStatus.value == HuaweiAuthManager.UserStatus.REGISTERED){
                    huaweiAuthManager.signOutUser()
                }
            }

            hiaweiDeleteUserBtn.setOnClickListener {
                huaweiAuthManager.deleteUser()
            }
        }
        initAuthManagerCallbacks(authManager)
        initSimpleAuthManagerCallbacks(simpleAuthManager)
        initHuaweiAuthManagerCallbacks(huaweiAuthManager)
    }

    private fun launchCredential(credential: FirebaseAuthCredential) {
        if (authManager.userStatus.value == AuthManager.UserStatus.REGISTERED
            && authManager.currentUser.value.containsCredential(credential)
        ) {
            if (!authManager.currentUser.value.isSingleCredential()) {
                authManager.removeCredentialFlow(credential)
                    .flowOn(Dispatchers.IO)
                    .catch { loginError(it) }
                    .onEach {
                        Toast.makeText(
                            this,
                            getString(R.string.remove_credential, credential.providerId),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .launchIn(CoroutineScope(Dispatchers.Main))
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.remove_simple_credential_error, credential.providerId),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            authManager.login(this, credential)
                .invokeOnCompletion { it?.cause?.let { loginError(it) } }
        }
    }

    private fun launchSimpleCredential(credential: BaseAuthCredential, isRemove: Boolean = false) {
        if (isRemove || simpleAuthManager.userStatus.value == SimpleAuthManager.UserStatus.REGISTERED
            && simpleAuthManager.currentUser.value.containsCredential(credential)
        ) {
            if (!simpleAuthManager.currentUser.value.isSingleCredential()) {
                simpleAuthManager.removeCredentialFlow(credential)
                    .flowOn(Dispatchers.IO)
                    .catch { simpleLoginError(it) }
                    .onEach {
                        Toast.makeText(
                            this,
                            getString(R.string.remove_credential, credential.providerId),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .launchIn(CoroutineScope(Dispatchers.Main))
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.remove_simple_credential_error, credential.providerId),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            simpleAuthManager.login(this, credential)
                .invokeOnCompletion { it?.cause?.let { simpleLoginError(it) } }
        }
    }

    private fun launchHuaweiCredential(credential: HuaweiAuthCredential, isRemove: Boolean = false) {
        huaweiAuthManager.signIn(credential)
    }

    private fun signInAnonymously() {
        authManager.signInAnonymously()
    }

    //TODO add logout logic for simple cred manager
    private fun credentialLogout() {
        authManager.logout().invokeOnCompletion { it?.cause?.let { loginError(it) } }
    }

    @SuppressLint("SetTextI18n")
    private fun loginError(throwable: Throwable?) {
        throwable?.let {
            binding.root.post {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                binding.logout.isEnabled =
                    authManager.currentUser.value.credentials.value.isNotEmpty()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun simpleLoginError(throwable: Throwable?) {
        throwable?.let {
            binding.root.post {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAuthManagerCallbacks(authManager: AuthManager) {
        authManager.currentUser
            .onEach {
                val resultCreds = authManager.currentUser.value.credentials.value
                binding.logout.isEnabled = resultCreds.isNotEmpty()
                binding.authResultCredentials.text =
                    if (resultCreds.isNotEmpty()) "User(${
                        authManager.userAccountName().firstOrNull()
                    }): ${resultCreds.map { it.providerId }}" else null
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }

    private fun initSimpleAuthManagerCallbacks(simpleAuthManager: SimpleAuthManager) {
        simpleAuthManager.currentUser
            .onEach {
                val resultCreds = simpleAuthManager.currentUser.value.credentials.value
                binding.simpleAuthResultCredentials.text =
                    if (resultCreds.isNotEmpty()) "Simple user: ${resultCreds.map { it.providerId }}"
                    else null
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }

    private fun initHuaweiAuthManagerCallbacks(huaweiAuthManager: HuaweiAuthManager) {
        huaweiAuthManager.currentUser
            .onEach {
                val resultCreds = huaweiAuthManager.currentUser.value
                binding.huaweiAuthResultCredentials.text =
                    "Huawei user: ${resultCreds.provider}"
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }

}