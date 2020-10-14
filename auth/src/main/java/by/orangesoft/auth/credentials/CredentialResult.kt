package by.orangesoft.auth.credentials

import by.orangesoft.auth.AuthMethod

data class CredentialResult(val method: AuthMethod, val token: String)