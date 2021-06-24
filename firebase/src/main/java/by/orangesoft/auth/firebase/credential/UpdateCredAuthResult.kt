package by.orangesoft.auth.firebase.credential

import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.parcel.Parcelize

@Parcelize
class UpdateCredAuthResult(private val currentUser: FirebaseUser, private val authCredential: AuthCredential) : AuthResult  {

    override fun getAdditionalUserInfo(): AdditionalUserInfo? = null
    override fun getUser(): FirebaseUser? = currentUser
    override fun getCredential(): AuthCredential? = authCredential
    override fun describeContents(): Int = 0

}