package co.orangesoft.authmanager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.BaseAuthManager
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.firebase.FirebaseUserController
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import co.orangesoft.authmanager.auth.SimpleAuthManager
import co.orangesoft.authmanager.auth.email.EmailAuthCredential
import co.orangesoft.authmanager.auth.phone.SimplePhoneAuthCredential
import co.orangesoft.authmanager.databinding.ActivityMainBinding
import co.orangesoft.authmanager.firebase_auth.AuthManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@InternalCoroutinesApi
class MainActivity : FragmentActivity() {

    private val authManager: AuthManager by lazy { AuthManager.getInstance(applicationContext, AuthManager.BASE_URL) }
    private val simpleAuthManager: SimpleAuthManager by lazy { SimpleAuthManager.getInstance(SimpleAuthManager.BASE_URL, applicationContext) }
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
            googleBtn.setOnClickListener { launchCredential(FirebaseAuthCredential.Google(getString(R.string.server_client_id))) }

            facebookBtn.setOnClickListener { launchCredential(FirebaseAuthCredential.Facebook) }

            appleBtn.setOnClickListener { launchCredential(FirebaseAuthCredential.Apple) }

            phoneBtn.setOnClickListener { launchCredential(FirebaseAuthCredential.Phone("+16505551234") { verificationId, forceResendingToken ->
                //TODO manually send code + verificationId
            }) }

            simplePhoneBtn.setOnClickListener { launchSimpleCredential(SimplePhoneAuthCredential("+375334445566", "1234")) }

            simpleEmailBtn.setOnClickListener { launchSimpleCredential(EmailAuthCredential("email@gmail.com", "password111")) }

            simpleRemoveEmailBtn.setOnClickListener { launchSimpleCredential(EmailAuthCredential("email@gmail.com", "password111"), true) }

            simpleRemovePhoneBtn.setOnClickListener { launchSimpleCredential(SimplePhoneAuthCredential("+375334445566", "1234"), true) }
        }
    }

    private fun launchCredential(credential: FirebaseAuthCredential) {

        initCallbacks(authManager)

        if (authManager.userStatus.value == AuthManager.UserStatus.REGISTERED && authManager.currentUser.value.containsCredential(credential)) {
            //TODO update removeCredentialFlow logic (add it?.cause in onCompletion)
            authManager.removeCredentialFlow(credential)
                .flowOn(Dispatchers.IO)
                .catch { loginError(it) }
                .onEach {
                    Toast.makeText(this, "RemoveCredential: ${credential.providerId}", Toast.LENGTH_SHORT).show()
                }
                .launchIn(CoroutineScope(Dispatchers.Main))
        } else {
            authManager.login(this, credential).invokeOnCompletion { it?.let { loginError(it.cause) } }
        }
    }

    private fun launchSimpleCredential(credential: BaseAuthCredential, isRemove: Boolean = false) {

        initCallbacks(simpleAuthManager)

        if (isRemove || simpleAuthManager.userStatus.value == SimpleAuthManager.UserStatus.REGISTERED && simpleAuthManager.currentUser.value.containsCredential(credential)) {
            //TODO update removeCredentialFlow logic (add it?.cause in onCompletion)
            simpleAuthManager.removeCredentialFlow(credential)
                .flowOn(Dispatchers.IO)
                .catch { loginError(it) }
                .onEach {
                    Toast.makeText(this, "RemoveCredential: ${credential.providerId}", Toast.LENGTH_SHORT).show()
                }
                .launchIn(CoroutineScope(Dispatchers.Main))
        } else {
            simpleAuthManager.login(this, credential).invokeOnCompletion { it?.let { loginError(it.cause) } }
        }
    }

    private fun signInAnonymously() {
        initCallbacks(authManager)
        authManager.signInAnonymously()
    }

    private fun loginError(throwable: Throwable?) {
        throwable?.let {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            val resultCreds = authManager.currentUser.value.credentials.value.toString()
            if (resultCreds.isNotEmpty()) {
                binding.resultCredentials.text = resultCreds
            }
        }
    }

    private fun initCallbacks(authManager: BaseAuthManager<*,*>) {
        authManager.currentUser
            .onEach {
                val resultCreds = authManager.currentUser.value.credentials.value
                binding.resultCredentials.text =
                    if (resultCreds.isNotEmpty()) resultCreds.toString()
                    else "GUEST USER ID: ${(it as FirebaseUserController).profile.uid}"
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }
}