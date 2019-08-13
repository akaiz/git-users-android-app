package com.example.kotlinapp.api.services

import com.example.kotlinapp.api.ApiResponse
import com.example.kotlinapp.api.AppException
import com.example.kotlinapp.api.Config
import com.example.kotlinapp.api.Exceptions
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("/search/users")
    fun getUsers(@Query("q") name: String): Call<UserResponse>

    @GET("/users/{name}")
    fun getUser(@Path("name") name: String): Call<AuthUserResponse>

    @GET("/developers?since=daily")
    fun getTrendingUsers(): Call<ArrayList<TrendingUserResponse>>
}

class UserResponse{
    @SerializedName("total_count")
    var total_count: Int? = null
    @SerializedName("items")
    var items: ArrayList<UserResponseItem>? = null
}
class UserResponseItem{
    @SerializedName("login")
    var name: String? = null
    @SerializedName("avatar_url")
    var imageUrl: String? = null
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("score")
    var score: Number? = null
}

class AuthUserResponse{
    @SerializedName("name")
    var name: String? = null
    @SerializedName("avatar_url")
    var imageUrl: String? = null
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("followers")
    var followers: Int? = null
}
class TrendingUserResponse{
    @SerializedName("name")
    var name: String? = null
    @SerializedName("username")
    var username: String? = null
    @SerializedName("avatar")
    var imageUrl: String? = null
}
class UserApi{

    fun getUser(name: String,callback:ApiResponse<AuthUserResponse>){
        val retrofit = Config().getGithubConfig()
        val service = retrofit.create(UserService::class.java)
        val call = service.getUser(name)

        call.enqueue(object : Callback<AuthUserResponse> {
            override fun onResponse(call: Call<AuthUserResponse>, response: Response<AuthUserResponse>) {
                if (response.code() == 200) {
                    val userResponse = response.body()!!
                    callback.success(userResponse)
                }
                else{
                    callback.error(AppException(Exceptions.INTERNAL_ERROR,"User name not found"))
                }

            }
            override fun onFailure(call: Call<AuthUserResponse>, t: Throwable) {
                callback.error(AppException(Exceptions.INTERNAL_ERROR,"Internal error"))
            }
        })

    }
    fun getTrendingUsers(name: String,callback:ApiResponse<ArrayList<TrendingUserResponse>>){
        val retrofit = Config().getTrendingConfig()
        val service = retrofit.create(UserService::class.java)
        val call = service.getTrendingUsers()

        call.enqueue(object : Callback<ArrayList<TrendingUserResponse>> {
            override fun onResponse(call: Call< ArrayList<TrendingUserResponse>>, response: Response< ArrayList<TrendingUserResponse>>) {
                if (response.code() == 200) {
                    val users = response.body()!!
                    callback.success(users)
                }
                else {
                    callback.error(AppException(Exceptions.NOT_FOUND,"Users  not found"))
                }

            }
            override fun onFailure(call: Call< ArrayList<TrendingUserResponse>>, t: Throwable) {
                callback.error(AppException(Exceptions.INTERNAL_ERROR,"Internal error"))
            }

        })

}
    }