package by.orangesoft.auth.firebase.credential

enum class CredentialsEnum(val className: String) {
    APPLE("co.orangesoft.apple.AppleCredentialController"),
    GOOGLE("co.orangesoft.google.GoogleCredentialController"),
    FACEBOOK("co.orangesoft.facebook.FacebookCredentialController"),
    PHONE("co.orangesoft.phone.PhoneCredentialController");

    companion object {
        fun getCredentialsPaths() = listOf(APPLE, GOOGLE, FACEBOOK, PHONE)
    }
}