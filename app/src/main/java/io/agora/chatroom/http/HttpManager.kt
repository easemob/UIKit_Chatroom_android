package io.agora.chatroom.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpManager {

    private var baseUrl = ""
    private var retrofit: Retrofit? = null

    fun <T> getService(clazz: Class<T>): T {
        return retrofit!!.create(clazz)
    }

    fun setBaseURL(url: String) {
        if (baseUrl == url) {
            return
        }
        baseUrl = url
        retrofit = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(LoggerInterceptor("ChatUrl", true, true))
                    .addInterceptor(TokenInterceptor())
                    .build()
            )
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
