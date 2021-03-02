package co.orangesoft.authmanager.auth.phone

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.AuthCredential

@SuppressLint("ParcelCreator")
data class SimplePhoneAuthCredential(val phone: String, val code: String) : AuthCredential("Phone")