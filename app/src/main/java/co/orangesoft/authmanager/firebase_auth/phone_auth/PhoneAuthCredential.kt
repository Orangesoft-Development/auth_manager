package co.orangesoft.authmanager.firebase_auth.phone_auth

import android.annotation.SuppressLint
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.FirebaseProviders

@SuppressLint("ParcelCreator")
data class PhoneAuthCredential(val phone: String, val code: String) :
    FirebaseAuthCredential(FirebaseProviders.PHONE)