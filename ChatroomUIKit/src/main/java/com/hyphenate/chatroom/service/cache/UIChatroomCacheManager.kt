package com.hyphenate.chatroom.service.cache

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import org.jetbrains.annotations.Nullable
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import android.util.Base64
import com.hyphenate.chatroom.service.UserEntity
import com.hyphenate.chatroom.service.model.UIConstant
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream

class UIChatroomCacheManager{
    private val memberMap: MutableMap<String, MutableList<String>> by lazy { mutableMapOf() }
    private val mutedMap: MutableMap<String, MutableList<String>> by lazy { mutableMapOf() }
    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private var userCache: MutableMap<String, UserEntity> = mutableMapOf()

    fun init(context: Context){
        mSharedPreferences = context.getSharedPreferences("SP_AT_PROFILE", Context.MODE_PRIVATE)
        mSharedPreferences.let {
            mEditor = it?.edit()
        }
    }

    fun saveUserInfo(userId:String,userInfo: UserEntity){
        userCache[userId] = userInfo
    }

    fun getUserInfo(userId:String):UserEntity{
        if (userCache.contains(userId)){
            return userCache[userId] ?: UserEntity(userId)
        }
        return UserEntity(userId)
    }

    /**
     * Judge whether the user information is in the cache
     */
    fun inCache(userId:String):Boolean{
        return userCache.contains(userId)
    }

    fun saveRoomMemberList(roomId: String, memberList: List<String>) {
        val list = memberMap[roomId] ?: mutableListOf()
        list.addAll(memberList)
        memberMap[roomId] = list.toSet().toMutableList()
    }

    fun getRoomMemberList(roomId: String): List<String> {
        return memberMap[roomId] ?: emptyList()
    }

    fun removeRoomMember(roomId: String, userId: String) {
        val list = memberMap[roomId] ?: mutableListOf()
        list.remove(userId)
        memberMap[roomId] = list
    }

    fun saveRoomMuteList(roomId: String, muteList: List<String>) {
        val list = mutedMap[roomId] ?: mutableListOf()
        list.addAll(muteList)
        mutedMap[roomId] = list.toSet().toMutableList()
    }

    fun removeRoomMuteMember(roomId: String, userId: String) {
        val list = mutedMap[roomId] ?: mutableListOf()
        list.remove(userId)
        mutedMap[roomId] = list
    }

    fun getRoomMuteList(roomId: String): List<String> {
        return mutedMap[roomId] ?: emptyList()
    }

    fun clearRoomUserCache() {
        memberMap.clear()
    }

    /**
     * Check whether the user is in the mute list
     */
    fun inMuteCache(roomId: String, userId:String):Boolean{
        return mutedMap.contains(roomId) && mutedMap[roomId]?.contains(userId) ?: false
    }

    fun setUseProperties(use:Boolean){
        putBoolean(UIConstant.CHATROOM_USE_PROPERTIES,use)
    }

    fun getUseProperties():Boolean{
        return getBoolean(UIConstant.CHATROOM_USE_PROPERTIES,false)
    }

    fun setCurrentTheme(isDark:Boolean){
        putBoolean(UIConstant.CHATROOM_THEME,isDark)
    }

    fun getCurrentTheme():Boolean{
        return getBoolean(UIConstant.CHATROOM_THEME,false)
    }

    /**
     * 存入字符串
     * @param key     字符串的键
     * @param value   字符串的值
     */
    @SuppressLint("ApplySharedPref")
    fun putString(key: String?, value: String?) {
        //存入数据
        mEditor?.putString(key, value)
        mEditor?.commit()
    }

    /**
     * 获取字符串
     * @param key     字符串的键
     * @return 得到的字符串
     */
    fun getString(key: String?): String? {
        return getString(key, "")
    }

    /**
     * 获取字符串
     * @param key     字符串的键
     * @param dv   字符串的默认值
     * @return 得到的字符串
     */
    private fun getString(key: String?, dv: String?): String? {
        return mSharedPreferences?.getString(key, dv)
    }

    /**
     * 保存布尔值
     * @param key     键
     * @param value   值
     */
    @SuppressLint("ApplySharedPref")
    fun putBoolean(key: String?, value: Boolean) {
        mEditor?.putBoolean(key, value)
        mEditor?.commit()
    }

    /**
     * 获取布尔值
     * @param key      键
     * @param defValue 默认值
     * @return 返回保存的值
     */
    private fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return mSharedPreferences?.getBoolean(key, defValue) ?: defValue
    }

    /**
     * 保存int值
     * @param key     键
     * @param value   值
     */
    @SuppressLint("ApplySharedPref")
    fun putInt(key: String?, value: Int) {
        mEditor?.putInt(key, value)
        mEditor?.commit()
    }

    /**
     * 获取int值
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    fun getInt(key: String?, defValue: Int): Int {
        return mSharedPreferences?.getInt(key, defValue) ?: defValue
    }

    /**
     * 存储List集合
     * @param key 存储的键
     * @param list 存储的集合
     */
    fun putList(key: String?, list: List<java.io.Serializable?>?) {
        putString(key, obj2Base64(list))
    }

    /**
     * 获取List集合
     * @param key 键
     * @param <E> 指定泛型
     * @return List集合
    </E> */
    @Nullable
    fun <E : Serializable?> getList(key: String?): List<E>? {
        return base64ToObj(getString(key)!!) as List<E>?
    }

    /**
     * 存储Map集合
     * @param key 键
     * @param map 存储的集合
     * @param <K> 指定Map的键
     * @param <V> 指定Map的值
    </V></K> */
    private fun <K : Serializable?, V> putMap(key: String?, map: MutableMap<K, V>?) {
        putString(key, obj2Base64(map))
    }

    /**
     * 获取map集合
     * @param key 键
     * @param <K> 指定Map的键
     * @param <V> 指定Map的值
     * @return 存储的集合
    </V></K> */
    @Nullable
    fun <K : Serializable?, V> getMap(key: String?): MutableMap<K, V>? {
        return base64ToObj(getString(key)!!) as MutableMap<K, V>?
    }

    /**
     * 对象转字符串
     * @param obj 任意对象
     * @return base64字符串
     */
    private fun obj2Base64(obj: Any?): String? {
        //判断对象是否为空
        if (obj == null) {
            return null
        }
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        var objectStr: String? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(obj)
            // 将对象放到OutputStream中
            // 将对象转换成byte数组，并将其进行base64编码
            objectStr = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (oos != null) {
                try {
                    oos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return objectStr
    }

    /**
     * base64转对象
     * @param base64 字符串
     * @param <T> 指定转成的类型
     * @return 指定类型对象 失败返回null
    </T> */
    private fun <T> base64ToObj(base64: String): T? {
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(base64)) {
            return null
        }
        val objBytes = Base64.decode(base64.toByteArray(), Base64.DEFAULT)
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        var t: T? = null
        try {
            bais = ByteArrayInputStream(objBytes)
            ois = ObjectInputStream(bais)
            t = ois.readObject() as T
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (bais != null) {
                try {
                    bais.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (ois != null) {
                try {
                    ois.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return t
    }

}