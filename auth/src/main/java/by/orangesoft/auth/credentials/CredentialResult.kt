package by.orangesoft.auth.credentials

open class CredentialResult(open val providerId: String, open val token: String)

open class UnlinkCredentialResult : CredentialResult("", "")