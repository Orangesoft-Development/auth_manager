package by.orangesoft.auth.credentials.firebase

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class AccountAuthenticator(val context: Context) : AbstractAccountAuthenticator(context) {

    override fun addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String?, requiredFeatures: Array<out String>?, options: Bundle?): Bundle? {
        Log.e("!!!", "addAccount")
        return null
    }


    override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle?): Bundle? {
        Log.e("!!!", "confirmCredentials")
        return null
    }


    override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle? {
        Log.e("!!!", "editProperties")
        return null
    }


    override fun getAuthToken(response: AccountAuthenticatorResponse, accountInput: Account, authTokenType: String, options: Bundle?): Bundle? {
        return Bundle().apply { putString("access", authTokenType) }
    }


    override fun getAuthTokenLabel(authTokenType: String): String? {
        Log.e("!!!", "getAuthTokenLabel")
        return null
    }


    override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<out String>): Bundle? {
        Log.e("!!!", "hasFeatures")
        return null
    }


    override fun updateCredentials(response: AccountAuthenticatorResponse, account: Account, authTokenType: String?, options: Bundle?): Bundle? {
        Log.e("!!!", "updateCredentials")
        return null
    }
}

class AccountAuthenticatorService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        val authenticator = AccountAuthenticator(this)
        return authenticator.iBinder
    }
}