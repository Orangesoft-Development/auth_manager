package co.orangesoft.authmanager.phone_auth.credentials

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.AuthCredential

@SuppressLint("ParcelCreator")
class PhoneAuthCredential(val phone: String, val code:String) : AuthCredential("Phone")