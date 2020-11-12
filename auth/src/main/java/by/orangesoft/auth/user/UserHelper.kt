package by.orangesoft.auth.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

object UserHelper {

    fun getAccessToken(): String {
        var token = ""
        runBlocking {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
                if  (it.isSuccessful) {
                    token = it.result?.token ?: ""
                } else {
                    Log.e("TOKEN", "Cannot get access token")
                }
            }
        }

        return token
    }
}