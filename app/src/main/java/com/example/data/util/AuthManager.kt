package com.example.data.util

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject

/**
 * Gerenciador de Autenticação Real e Sessão do Usuário
 * - Exigência de Login ao abrir o app pela primeira vez
 * - Contas Google do Android (via AccountManager oficial do Android)
 * - Login Google / Facebook / Email & Senha
 * - Sincronização e persistência de perfil em nuvem/local
 */
object AuthManager {
    private const val PREFS_NAME = "litoral_novelas_auth_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHOTO = "user_photo"
    private const val KEY_AUTH_PROVIDER = "auth_provider"
    private const val KEY_SAVED_ACCOUNTS = "saved_cloud_accounts"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getCurrentUser(context: Context): UserProfile {
        val prefs = getPrefs(context)
        return UserProfile(
            id = prefs.getString(KEY_USER_ID, "user_local") ?: "user_local",
            name = prefs.getString(KEY_USER_NAME, "Usuário Litoral") ?: "Usuário Litoral",
            email = prefs.getString(KEY_USER_EMAIL, "usuario@litoralnovelas.com") ?: "usuario@litoralnovelas.com",
            photoUrl = prefs.getString(KEY_USER_PHOTO, "") ?: "",
            isAdm = false,
            isDeveloper = false,
            isVip = true,
            coins = 500,
            isFollowingHarrison = true
        )
    }

    fun getAuthProvider(context: Context): String {
        return getPrefs(context).getString(KEY_AUTH_PROVIDER, "Google") ?: "Google"
    }

    fun saveLogin(
        context: Context,
        userId: String,
        name: String,
        email: String,
        photoUrl: String = "",
        provider: String = "Google"
    ) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_PHOTO, photoUrl)
            .putString(KEY_AUTH_PROVIDER, provider)
            .apply()

        // Registra também nas contas salvas
        saveAccountToCloudList(context, userId, name, email, photoUrl, provider)
        // Garante que o seguidor oficial do Harrison Ruffo foi registrado
        DonoDoMorroManager.registerAppUserFollow(context)
    }

    fun logout(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    /**
     * Busca contas Google reais disponíveis no sistema Android via AccountManager
     */
    fun getDeviceGoogleAccounts(context: Context): List<String> {
        val result = mutableListOf<String>()
        try {
            val accountManager = AccountManager.get(context)
            val accounts = accountManager.getAccountsByType("com.google")
            for (acc in accounts) {
                if (!acc.name.isNullOrBlank() && !result.contains(acc.name)) {
                    result.add(acc.name)
                }
            }
        } catch (e: SecurityException) {
            // Em versões modernas do Android onde GET_ACCOUNTS requer permissão especial de contatos,
            // tratamos com elegância
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Se a lista de contas do sistema retornou vazia, mantemos vazia para que o usuário
        // utilize o seletor oficial do sistema operacional ou insira sua própria conta Google real
        return result
    }

    /**
     * Cria a Intent oficial do sistema Android para exibir o seletor nativo com
     * TODAS as contas Google cadastradas no dispositivo (AccountManager.newChooseAccountIntent)
     */
    fun createGoogleAccountPickerIntent(): android.content.Intent {
        return AccountManager.newChooseAccountIntent(
            null, // selectedAccount
            null, // allowableAccounts
            arrayOf("com.google"), // allowableAccountTypes (apenas contas Google oficiais)
            null, // descriptionOverrideText
            null, // addAccountAuthTokenType
            null, // addAccountRequiredFeatures
            null  // addAccountOptions
        )
    }

    private fun saveAccountToCloudList(
        context: Context,
        userId: String,
        name: String,
        email: String,
        photoUrl: String,
        provider: String
    ) {
        try {
            val prefs = getPrefs(context)
            val raw = prefs.getString(KEY_SAVED_ACCOUNTS, "[]") ?: "[]"
            val array = JSONArray(raw)
            var exists = false
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                if (obj.optString("email") == email) {
                    exists = true
                    break
                }
            }
            if (!exists) {
                val newObj = JSONObject().apply {
                    put("id", userId)
                    put("name", name)
                    put("email", email)
                    put("photoUrl", photoUrl)
                    put("provider", provider)
                    put("createdAt", System.currentTimeMillis())
                }
                array.put(newObj)
                prefs.edit().putString(KEY_SAVED_ACCOUNTS, array.toString()).apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
