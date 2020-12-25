package co.orangesoft.authmanager.auth.phone_auth.credentials

import android.annotation.SuppressLint
import by.orangesoft.auth.firebase.credential.Firebase

@SuppressLint("ParcelCreator")
data class PhoneAuthCredential(val phone: String, val code:String) : Firebase("Phone")