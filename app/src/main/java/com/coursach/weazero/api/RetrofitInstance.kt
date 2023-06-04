package com.coursach.weazero.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.coursach.weazero.api.WeatherApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }


    val api: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
