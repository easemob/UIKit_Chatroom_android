package io.agora.chatroom.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import io.agora.chatroom.http.LoginReq
import io.agora.chatroom.http.toJson
import io.agora.chatroom.model.UserInfoProtocol

class SPUtils private constructor(private val context: Application) {

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences("ui_user_info", Context.MODE_PRIVATE)
    }

    companion object {
        private var instance: SPUtils? = null
        fun getInstance(context: Application): SPUtils {
            if (instance == null) {
                synchronized(SPUtils::class.java) {
                    if (instance == null) {
                        instance = SPUtils(context)
                    }
                }
            }
            return instance!!
        }
    }

    fun saveUserInfo(userInfo: LoginReq) {
        sp.edit().putString("userInfo", userInfo.toJson()).apply()
    }

    fun getUerInfo(): UserInfoProtocol? {
        return sp.getString("userInfo", null)?.let {
            Gson().fromJson(it, UserInfoProtocol::class.java)
        }
    }

    fun saveToken(token: String) {
        sp.edit().putString("token", token).apply()
    }

    fun getToken(): String {
        return sp.getString("token", "") ?: ""
    }

    fun saveCurrentThemeStyle(isDark: Boolean) {
        sp.edit().putBoolean("isDark", isDark).apply()
    }

    fun getCurrentThemeStyle(): Boolean {
        return sp.getBoolean("isDark", false)
    }

}