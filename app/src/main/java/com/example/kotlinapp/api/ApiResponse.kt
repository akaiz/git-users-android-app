package com.example.kotlinapp.api

interface ApiResponse<T> {
 fun success(response:T)
 fun error(error:AppException?)
}