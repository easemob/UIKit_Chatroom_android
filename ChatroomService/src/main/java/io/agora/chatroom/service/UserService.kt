package io.agora.chatroom.service

import io.agora.chatroom.model.UserInfoProtocol


interface UserService {
    fun bindUserStateChangeListener(listener: UserStateChangeListener)
    fun unbindUserStateChangeListener(listener: UserStateChangeListener)

    fun getUserInfo(userId: String, onSuccess: OnValueSuccess<UserInfoProtocol>, onError: OnError)

    fun getUserInfoList(userIdList: List<String>, onSuccess: OnValueSuccess<List<UserInfoProtocol>>, onError: OnError)

    fun updateUserInfo(userEntity: UserInfoProtocol, onSuccess: OnSuccess, onError: OnError)

    fun login(userId: String, token: String, onSuccess: OnSuccess, onError: OnError)

    fun login(user: UserInfoProtocol, token: String, onSuccess: OnSuccess, onError: OnError)

    fun logout(onSuccess: OnSuccess, onFailure: OnError)
}

interface UserStateChangeListener {

    /**
     * Occurs when the SDK connects to the chat server successfully.
     */
    fun onConnected()

    /**
     * Occurs when the SDK disconnect from the chat server.
     * Note that the logout may not be performed at the bottom level when the SDK is disconnected.
     */
    fun onDisconnected(error: Int)

    /**
     * Occurs when the token has expired.
     */
    fun onTokenExpired() {}

    /**
     * Occurs when the token is about to expire.
     */
    fun onTokenWillExpire() {}

    /**
     * Occurs when the UNDERLYING SDK logs out.
     * @param errorCode error code .
     *  Common errors are as follows:
     *  {@link ChatError#USER_LOGIN_ANOTHER_DEVICE}，{@link ChatError#USER_REMOVED}，
     *  {@link ChatError#USER_BIND_ANOTHER_DEVICE}，{@link ChatError#SERVER_SERVICE_RESTRICTED}，
     *  {@link ChatError#USER_LOGIN_TOO_MANY_DEVICES}，{@link ChatError#USER_KICKED_BY_CHANGE_PASSWORD}，
     *  {@link ChatError#USER_KICKED_BY_OTHER_DEVICE}
     * @param info error extend info .
     */
    fun onLogout(errorCode: Int, info: String?) {}

}

data class UserEntity(
    val userId: String,
    var nickName: String? = "",
    val avatarURL: String? = "",
    val gender: Int = 0,
    val identify: String? = "",
)

fun ChatUserInfo.transfer() = UserEntity(userId, nickname, avatarUrl, gender,ext )
fun UserEntity.transfer(): UserInfoProtocol = UserInfoProtocol(userId, nickName, avatarURL, gender, identify)