package io.agora.chatroom.http

import android.text.TextUtils
import io.agora.chatroom.service.ChatLog
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException


/**
 * Created by songyuqiang on 16/11/3.
 */
class LoggerInterceptor(
    tag: String?,
    showResponse: Boolean,
    private val mShowRequest: Boolean
) : Interceptor {
    private var mTag: String? = null
    private val mShowResponse: Boolean

    init {
        if (TextUtils.isEmpty(tag)) {
            mTag = TAG
        } else {
            mTag = tag
        }
        mShowResponse = showResponse
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (mShowRequest) {
            logForRequest(request)
        }
        val response: Response = chain.proceed(request)
        return logForResponse(response)
    }

    private fun logForResponse(response: Response): Response {
        if (!mShowResponse) {
            return response
        }
        try {
            //===>response log
            ChatLog.e(mTag, "========response'log=======")
            val builder: Response.Builder = response.newBuilder()
            val clone: Response = builder.build()
            ChatLog.e(mTag, "url : " + clone.request.url)
            ChatLog.e(mTag, "code : " + clone.code)
            ChatLog.e(mTag, "protocol : " + clone.protocol)
            if (!TextUtils.isEmpty(clone.message)) ChatLog.e(mTag, "message : " + clone.message)
            if (mShowResponse) {
                var body = clone.body
                if (body != null) {
                    val mediaType = body.contentType()
                    if (mediaType != null) {
                        ChatLog.e(mTag, "responseBody's contentType : $mediaType")
                        if (isText(mediaType)) {
                            val resp = body.string()
                            ChatLog.e(mTag, "responseBody's content : $resp")
                            body = ResponseBody.create(mediaType, resp)
                            return response.newBuilder().body(body).build()
                        } else {
                            ChatLog.e(
                                mTag,
                                "responseBody's content : " + " maybe [file part] , too large too print , ignored!"
                            )
                        }
                    }
                }
            }
            ChatLog.e(mTag, "========response'log=======end")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun logForRequest(request: Request) {
        try {
            val url = request.url.toString()
            val headers = request.headers
            ChatLog.e(mTag, "========request'log=======")
            ChatLog.e(mTag, "method : " + request.method)
            ChatLog.e(mTag, "url : $url")
            if (headers != null && headers.size > 0) {
                ChatLog.e(mTag, "headers : $headers")
            }
            val requestBody = request.body
            if (requestBody != null) {
                val mediaType = requestBody.contentType()
                if (mediaType != null) {
                    ChatLog.e(mTag, "requestBody's contentType : $mediaType")
                    if (isText(mediaType)) {
                        ChatLog.e(mTag, "requestBody's content : " + bodyToString(request))
                    } else {
                        ChatLog.e(
                            mTag,
                            "requestBody's content : " + " maybe [file part] , too large too print , ignored!"
                        )
                    }
                }
            }
            ChatLog.e(mTag, "========request'log=======end")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isText(mediaType: MediaType): Boolean {
        if (mediaType.type != null && mediaType.type == "text") {
            return true
        }
        if (mediaType.subtype != null) {
            if (mediaType.subtype == "json" || mediaType.subtype == "xml" || mediaType.subtype == "html" || mediaType.subtype == "webviewhtml") return true
        }
        return false
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body!!.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            e.printStackTrace()
            "something error when show requestBody."
        }
    }

    companion object {
        const val TAG = "OkHttpResponse"
    }
}