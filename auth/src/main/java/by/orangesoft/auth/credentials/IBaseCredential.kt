package by.orangesoft.auth.credentials


/**
 * Basic interface for authorization credential
 *
 * @property providerId Id of authorization credential
 * @see BaseAuthCredential
 *
 */
interface IBaseCredential {
    val providerId: String
}