package co.orangesoft.authmanager.api

import androidx.annotation.StringDef
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.Firebase
import co.orangesoft.authmanager.credential.PhoneCredential

sealed class ApiCredentials(@AuthMethod val method: String) {

    class Phone(val firebase_id: String?, val phone: String, val sms_code: String, @AuthMethod method: String = AuthMethod.PHONE) : ApiCredentials(method) {
        companion object {
            fun fromPhoneCredential(id: String?, credential: PhoneCredential): Phone = Phone(id, credential.phone, credential.code)
        }
    }

    sealed class Social(
        @AuthMethod method: String,
        val access_token: String
    ) : ApiCredentials(method) {

        class Google(accessToken: String) : Social(AuthMethod.GOOGLE, accessToken)
        class Facebook(accessToken: String) : Social(AuthMethod.FACEBOOK, accessToken)
        class Apple(accessToken: String) : Social(AuthMethod.APPLE, accessToken)
    }

    sealed class ApiFirebase(
        @FirebaseMethod val social: String,
        val access_token: String
    ) : ApiCredentials(AuthMethod.FIREBASE) {

        class Google(accessToken: String) : ApiFirebase(FirebaseMethod.GOOGLE, accessToken)
        class Facebook(accessToken: String) : ApiFirebase(FirebaseMethod.FACEBOOK, accessToken)
        class Apple(accessToken: String) : ApiFirebase(FirebaseMethod.APPLE, accessToken)
        class Phone(accessToken: String) : ApiFirebase(FirebaseMethod.PHONE, accessToken)
        class Anonimus(accessToken: String) : ApiFirebase(FirebaseMethod.ANONIMUS, accessToken)
    }

    companion object {
        fun fromCredentialResult(result: CredentialResult): ApiCredentials =
            when(result.method){
                is Firebase.Apple       -> ApiFirebase.Apple(result.token)
                is Firebase.Google      -> ApiFirebase.Google(result.token)
                is Firebase.Facebook    -> ApiFirebase.Facebook(result.token)
                is PhoneCredential      -> {
                    if(result.token.isNullOrBlank())
                        Phone(null, (result.method as PhoneCredential).phone, (result.method as PhoneCredential).code)
                    else
                        ApiFirebase.Phone(result.token)
                }
                else                    -> ApiFirebase.Anonimus(result.token)
            }
    }
}



@StringDef(value = [AuthMethod.PHONE, AuthMethod.GOOGLE, AuthMethod.FACEBOOK, AuthMethod.APPLE, AuthMethod.FIREBASE])
annotation class AuthMethod {

    companion object {
        const val PHONE = "phone"
        const val GOOGLE = "google"
        const val FACEBOOK = "facebook"
        const val APPLE = "apple"
        const val FIREBASE = "firebase"
    }
}

@StringDef(value = [FirebaseMethod.GOOGLE, FirebaseMethod.FACEBOOK, FirebaseMethod.APPLE, FirebaseMethod.PHONE, FirebaseMethod.ANONIMUS])
annotation class FirebaseMethod {

    companion object {
        const val GOOGLE = "google"
        const val FACEBOOK = "facebook"
        const val PHONE = "phone"
        const val APPLE = "apple"
        const val ANONIMUS = "anonimus"
    }
}

