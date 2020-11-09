package co.orangesoft.authmanager

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import by.orangesoft.auth.credentials.firebase.Firebase
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.user.Profile
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val loginSuccessListener: (FirebaseUserController<Profile>) -> Unit  = {
        Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show()
    }

    private val loginErrorListener: (Throwable) -> Unit = {
        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

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
        val authManager = AuthManager.getInstance()

        googleBtn.setOnClickListener {
            authManager.login(this, Firebase.Google(getString(R.string.server_client_id))) {
                onAuthException(loginErrorListener)
                onAuthSuccess(loginSuccessListener)
            }
        }

        facebookBtn.setOnClickListener {
            authManager.login(this, Firebase.Facebook) {
                onAuthException(loginErrorListener)
                onAuthSuccess(loginSuccessListener)
            }
        }

        appleBtn.setOnClickListener {
            authManager.login(this, Firebase.Apple) {
                onAuthException(loginErrorListener)
                onAuthSuccess(loginSuccessListener)
            }
        }
    }
}