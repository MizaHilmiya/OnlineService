package id.ac.polbeng.mizahilmiya.onlineservice.services

import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    //create logger
    private val logger: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    //create okhttp client
    private val okHttp: OkHttpClient.Builder = OkHttpClient.Builder()
        .callTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(logger)

    //create retrofit builder
    private val builder: Retrofit.Builder =
        Retrofit.Builder().baseUrl(Config.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build())

    //create retrofit instance
    private val retrofit: Retrofit = builder.build()
    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}