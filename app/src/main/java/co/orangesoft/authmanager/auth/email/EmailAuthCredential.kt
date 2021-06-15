package co.orangesoft.authmanager.auth.email

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.BaseAuthCredential

@SuppressLint("ParcelCreator")
data class EmailAuthCredential(val email: String, val password: String) : BaseAuthCredential("Email")