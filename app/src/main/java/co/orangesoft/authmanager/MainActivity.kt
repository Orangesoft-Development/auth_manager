package co.orangesoft.authmanager

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.firebase_auth.AuthManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

@InternalCoroutinesApi
class MainActivity : FragmentActivity() {

    private val loginSuccessListener: (FirebaseUserController) -> Unit  = {
        Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show()
    }

    private val loginErrorListener: (Throwable) -> Unit = {
        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
    }

    private val authManager: AuthManager by lazy { AuthManager.getInstance(AuthManager.BASE_URL) }

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
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view -> }

        googleBtn.setOnClickListener { launchCredential(Firebase.Google(getString(R.string.server_client_id))) }

        facebookBtn.setOnClickListener { launchCredential(Firebase.Facebook) }

        appleBtn.setOnClickListener { launchCredential(Firebase.Apple) }
    }

    private fun launchCredential(credential: Firebase){
        if(authManager.userStatus.value == AuthManager.UserStatus.REGISTERED && authManager.currentUser.value.credentials.value.contains(credential))
            authManager.removeCredential(credential)
                    .invokeOnCompletion { error ->
                        error?.apply { loginErrorListener(error) } ?: loginSuccessListener(authManager.currentUser.value)
                    }
        else
            authManager.login(this, credential)
                    .invokeOnCompletion { error ->
                        error?.apply { loginErrorListener(error) } ?: loginSuccessListener(authManager.currentUser.value)
                    }
    }
}