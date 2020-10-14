package co.orangesoft.authmanager.credential

import android.annotation.SuppressLint
import by.orangesoft.auth.AuthMethod

@SuppressLint("ParcelCreator")
data class PhoneCredential(val phone: String, val code:String): AuthMethod("phone")
