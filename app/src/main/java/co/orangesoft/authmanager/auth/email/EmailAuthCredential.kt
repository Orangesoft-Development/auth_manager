package co.orangesoft.authmanager.auth.email

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.AuthCredential

@SuppressLint("ParcelCreator")
data class EmailAuthCredential(val email: String, val password: String) : AuthCredential("Email")