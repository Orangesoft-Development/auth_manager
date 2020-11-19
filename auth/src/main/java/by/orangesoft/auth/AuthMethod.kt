package by.orangesoft.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class AuthMethod(val providerId: String) : Parcelable {

    override fun equals(other: Any?): Boolean =
        when(other) {
            is AuthMethod   -> providerId == other.providerId
            is String       -> providerId == other
            else            ->  super.equals(other)
        }

    override fun hashCode(): Int = providerId.hashCode()

    override fun toString(): String = "${javaClass.simpleName}($providerId)"
}