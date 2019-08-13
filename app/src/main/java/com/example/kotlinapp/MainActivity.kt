package com.example.kotlinapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.kotlinapp.api.Config
import com.example.kotlinapp.api.services.TrendingUserResponse
import com.example.kotlinapp.api.services.UserResponse
import com.example.kotlinapp.api.services.UserService
import com.example.kotlinapp.dtos.UserDto
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var users: ArrayList<UserDto>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        refresh_button.visibility = View.GONE
        search_users_loader.visibility = View.GONE

        refresh_button.setOnClickListener {
            reload()
        }

        getTrendingUsers()

        onSearchButtonClicked()

    }

    private fun onSearchButtonClicked() {
        search_button.setOnClickListener {
            if (edit_text_name.text.isNotEmpty()) {
                val view = this.currentFocus
                view?.let { v ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
                }
                edit_text_name.visibility = View.GONE
                search_users_loader.visibility = View.VISIBLE
                search_button.visibility = View.GONE
                searchUsers()
            } else {
                Toast.makeText(this@MainActivity, "Enter name !", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun searchUsers() {
        val retrofit = Config().getGithubConfig()
        val service = retrofit.create(UserService::class.java)
        val call = service.getUsers(edit_text_name.text.toString())

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {

                if (response.code() == 200) {
                    val userResponse = response.body()!!
                    if (userResponse.total_count!! > 0) {
                        val intent = Intent(this@MainActivity, UsersActivity::class.java)

                        users = ArrayList()
                        userResponse.items!!.forEach {
                            users?.add(
                                UserDto(
                                    it.name!!,
                                    it.id!!,
                                    it.imageUrl!!,
                                    it.score!!
                                )
                            )
                        }
                        intent.putExtra("users", Gson().toJson(users))
                        intent.putExtra("user_param",edit_text_name.text.toString())
                        search_button.visibility = View.VISIBLE
                        edit_text_name.visibility = View.VISIBLE
                        search_users_loader.visibility = View.GONE
                        startActivity(intent)
                    } else {
                        search_button.visibility = View.VISIBLE
                        edit_text_name.visibility = View.VISIBLE
                        search_users_loader.visibility = View.GONE
                        Toast.makeText(this@MainActivity, "Can't get users ", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Can't get users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTrendingUsers() {
        val retrofit = Config().getTrendingConfig()
        val service = retrofit.create(UserService::class.java)
        val call = service.getTrendingUsers()


        call.enqueue(object : Callback<ArrayList<TrendingUserResponse>> {
            override fun onResponse(
                call: Call<ArrayList<TrendingUserResponse>>,
                response: Response<ArrayList<TrendingUserResponse>>
            ) {

                if (response.code() == 200) {
                    val users = response.body()!!
                    val position = (0 until users.size).toList().toTypedArray().random()
                    val user = users[position]
                    trending_user_loader.visibility = View.GONE
                    trending_user_image.visibility = View.VISIBLE
                    Picasso.get().load(user.imageUrl).transform(CropCircleTransformation()).into(trending_user_image)
                    trending_user_name.text = user.name
                    trending_user_position.text = "# $position"
                    refresh_button.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ArrayList<TrendingUserResponse>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Can't get users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun reload() {
        trending_user_loader.visibility = View.VISIBLE
        refresh_button.visibility = View.GONE
        trending_user_image.visibility = View.GONE
        trending_user_position.text = ""
        trending_user_name.text = ""
        this.getTrendingUsers()

    }
}
