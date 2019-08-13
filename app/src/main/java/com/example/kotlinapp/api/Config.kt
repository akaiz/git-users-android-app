package com.example.kotlinapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Config{

    fun getGithubConfig():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    fun getTrendingConfig():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://github-trending-api.now.sh")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}