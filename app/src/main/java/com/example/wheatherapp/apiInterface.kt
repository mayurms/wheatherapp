package com.example.wheatherapp


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface apiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") q: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<WheatherApi>

}