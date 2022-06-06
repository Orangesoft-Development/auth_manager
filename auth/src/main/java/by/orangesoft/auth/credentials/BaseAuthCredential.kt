package by.orangesoft.auth.credentials

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * This is an open class of authorization credential type
 *
 * @param providerId Id of authorization credential
 *
 */

@Parcelize
open class BaseAuthCredential(override val providerId: String) : IBaseCredential, Parcelable {

    constructor(credential: IBaseCredential) : this(credential.providerId)

    override fun equals(other: Any?): Boolean =
        when (other) {
            is BaseAuthCredential -> providerId == other.providerId
            is IBaseCredential -> providerId == other.providerId
            is String -> providerId == other
            else -> super.equals(other)
        }

    override fun hashCode(): Int = providerId.hashCode()

    override fun toString(): String = "${javaClass.simpleName}($providerId)"
}
