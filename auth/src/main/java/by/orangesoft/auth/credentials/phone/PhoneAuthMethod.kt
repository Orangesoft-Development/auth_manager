package by.orangesoft.auth.credentials.phone

import android.annotation.SuppressLint
import by.orangesoft.auth.AuthMethod

@SuppressLint("ParcelCreator")
data class PhoneAuthMethod(val phone: String, val code:String): AuthMethod("phone")
