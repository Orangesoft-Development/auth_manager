package co.orangesoft.authmanager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.BaseAuthManager
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.FirebaseUserController
import by.orangesoft.auth.user.IBaseUserController
import co.orangesoft.authmanager.auth.SimpleAuthManager
import co.orangesoft.authmanager.auth.email.EmailAuthCredential
import co.orangesoft.authmanager.auth.phone.SimplePhoneAuthCredential
import co.orangesoft.authmanager.firebase_auth.AuthManager
import co.orangesoft.authmanager.firebase_auth.phone_auth.PhoneAuthCredential
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@InternalCoroutinesApi
class MainActivity : FragmentActivity() {

    private val authManager: AuthManager by lazy { AuthManager.getInstance(AuthManager.BASE_URL) }
    private val simpleAuthManager: SimpleAuthManager by lazy { SimpleAuthManager.getInstance(SimpleAuthManager.BASE_URL, applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
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

        googleBtn.setOnClickListener { launchCredential(Firebase.Google(getString(R.string.server_client_id))) }

        facebookBtn.setOnClickListener { launchCredential(Firebase.Facebook) }

        appleBtn.setOnClickListener { launchCredential(Firebase.Apple) }

        phoneBtn.setOnClickListener { launchCredential(PhoneAuthCredential("+375334445566", "1234")) }

        simplePhoneBtn.setOnClickListener { launchSimpleCredential(SimplePhoneAuthCredential("+375334445566", "1234")) }

        simpleEmailBtn.setOnClickListener { launchSimpleCredential(EmailAuthCredential("email@gmail.com", "password111")) }

        simpleRemoveEmailBtn.setOnClickListener { launchSimpleCredential(EmailAuthCredential("email@gmail.com", "password111"), true) }

        simpleRemovePhoneBtn.setOnClickListener { launchSimpleCredential(SimplePhoneAuthCredential("+375334445566", "1234"), true) }
    }

    private fun launchCredential(credential: Firebase) {

        initCallbacks(authManager)

        if (authManager.userStatus.value == AuthManager.UserStatus.REGISTERED && authManager.currentUser.value.credentials.value.contains(credential)) {
            authManager.removeCredential(credential)
        } else {
            authManager.login(this, credential)
        }
    }

    private fun launchSimpleCredential(credential: AuthCredential, isRemove: Boolean = false) {

        initCallbacks(simpleAuthManager)

        if (isRemove || simpleAuthManager.userStatus.value == SimpleAuthManager.UserStatus.REGISTERED && simpleAuthManager.currentUser.value.credentials.value.contains(credential)) {
            simpleAuthManager.removeCredential(credential)
        } else {
            simpleAuthManager.login(this, credential)
        }
    }

    private fun initCallbacks(authManager: BaseAuthManager<*,*>) {

        val loginSuccessListener: (IBaseUserController<*>) -> Unit  = {
            val resultCreds = simpleAuthManager.currentUser.value.credentials.value.toString()
            if (resultCreds.isNotEmpty()) {
                resultCredentials.text = resultCreds
            }
        }

        val loginErrorListener: (Throwable) -> Unit = {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            val resultCreds = simpleAuthManager.currentUser.value.credentials.value.toString()
            if (resultCreds.isNotEmpty()) {
                resultCredentials.text = resultCreds
            }
        }

        authManager.currentUser.onEach {
            loginSuccessListener(authManager.currentUser.value)
        }.catch {
            loginErrorListener(it)
        }.launchIn(CoroutineScope(Dispatchers.Main))
    }
}