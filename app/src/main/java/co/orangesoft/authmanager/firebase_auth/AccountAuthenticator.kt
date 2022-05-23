package co.orangesoft.authmanager.firebase_auth

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class AccountAuthenticator(val context: Context) : AbstractAccountAuthenticator(context) {

    private val TAG = "AccountAuthenticator"

    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle? {
        val pkg = ComponentName(context.packageName, context.packageName + ".ui.MainActivity")
        val intent = Intent("android.intent.action.MAIN").apply {
            putExtra("destination", "user")
        }
        intent.component = pkg
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        return null
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle?
    ): Bundle? {
        Log.e(TAG, "confirmCredentials")
        return null
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle? {
        Log.e(TAG, "editProperties")
        return null
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        accountInput: Account,
        authTokenType: String,
        options: Bundle?
    ): Bundle {
        return Bundle().apply { putString(authTokenType, authTokenType) }
    }

    override fun getAuthTokenLabel(authTokenType: String): String? {
        Log.e(TAG, "getAuthTokenLabel")
        return null
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<out String>
    ): Bundle? {
        Log.e(TAG, "hasFeatures")
        return null
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        Log.e(TAG, "updateCredentials")
        return null
    }
}

class AccountAuthenticatorService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        val authenticator = AccountAuthenticator(this)
        return authenticator.iBinder
    }
}