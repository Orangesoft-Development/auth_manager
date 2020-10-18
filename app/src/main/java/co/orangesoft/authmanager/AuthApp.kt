package co.orangesoft.authmanager

import android.app.Application
import com.google.firebase.FirebaseApp

class AuthApp : Application() {

    override fun onCreate() {

        FirebaseApp.initializeApp(this)

        super.onCreate()

    }

}