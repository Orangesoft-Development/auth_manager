package co.orangesoft.authmanager

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class AuthApp : Application() {

    override fun onCreate() {
        initFirebase()
        super.onCreate()
    }

    private fun initFirebase() {
        if (isFirebaseInitialized()) {
            return
        }
        val options = FirebaseOptions.fromResource(this)
        if (options == null) {
            FirebaseApp.initializeApp(this)
        } else {
            FirebaseApp.initializeApp(this, options)
        }
    }

    private fun isFirebaseInitialized(): Boolean {
        return try {
            FirebaseApp.getInstance()
            true
        } catch (e: Exception) {
            false
        }
    }

}