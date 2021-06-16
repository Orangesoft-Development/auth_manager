package co.orangesoft.authmanager.auth.phone

import android.annotation.SuppressLint
import by.orangesoft.auth.credentials.BaseAuthCredential

@SuppressLint("ParcelCreator")
data class SimplePhoneAuthCredential(val phone: String, val code: String) : BaseAuthCredential("Phone")