package com.example.kotlinapp.api

enum class Exceptions {
    NOT_FOUND,INTERNAL_ERROR
}

class AppException(val type:Exceptions, val value:Any)