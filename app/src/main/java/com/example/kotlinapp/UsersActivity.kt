package com.example.kotlinapp

import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.widget.*
import com.example.kotlinapp.adapters.CustomAdapter
import com.example.kotlinapp.api.ApiResponse
import com.example.kotlinapp.api.AppException
import com.example.kotlinapp.api.Exceptions
import com.example.kotlinapp.api.services.AuthUserResponse
import com.example.kotlinapp.api.services.UserApi
import com.example.kotlinapp.dtos.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_users.*


class UsersActivity : AppCompatActivity() {

    private lateinit var viewManager: RecyclerView.LayoutManager

    private val users= ArrayList<UserDto>()
    var adapter:CustomAdapter?=null
    var recyclerView:RecyclerView?=null
    var dialog:Dialog?=null
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        setSupportActionBar(toolbar)

         initialize()

        usersListing()

        userDialog()

        fab.setOnClickListener { view ->
            adapter!!.filter.filter("")
            searchView!!.isIconified=true
            searchView!!.clearFocus()
            Snackbar.make(view, "Restored", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


    }

    private fun userDialog() {
        dialog = Dialog(this)
        dialog?.setContentView(R.layout.user_popup)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val closeButton = dialog?.findViewById<ImageButton>(R.id.close_button)
        closeButton?.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun initialize(){
        var bundle = intent.getStringExtra("users")
        var userParam = intent.getStringExtra("user_param")
        val listType = object : TypeToken<ArrayList<UserDto>>() {}.type
        users.addAll(Gson().fromJson<ArrayList<UserDto>>(bundle, listType))
        users.forEach { Log.d("Name",it.name) }
        val actionBar = supportActionBar
        actionBar!!.title=" Users : $userParam"
    }

    private fun usersListing() {
        viewManager = LinearLayoutManager(this)
        adapter = CustomAdapter(users,object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(item: UserDto) {
                UserApi().getUser(item.name  ,object : ApiResponse<AuthUserResponse> {
                    override fun success(response: AuthUserResponse) {

                        Picasso.get().load(item?.image).transform(CropCircleTransformation())
                               .into(dialog?.findViewById<ImageView>(R.id.user_image))
                        dialog?.findViewById<TextView>(R.id.user_name)?.text=response.name.toString()
                        dialog?.findViewById<TextView>(R.id.user_followers)?.text=response.followers.toString()
                        dialog?.show()
                    }
                    override fun error(error: AppException?) {
                        if (error?.type== Exceptions.NOT_FOUND)
                            Toast.makeText(this@UsersActivity,"User Not Found",Toast.LENGTH_SHORT).show()
                    }

                })
            }
        })

         recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter =this@UsersActivity.adapter
        }
        adapter?.notifyDataSetChanged()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.users_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView

        if(searchView!=null){
            searchView!!.setSearchableInfo(searchManager
                .getSearchableInfo(componentName))
            searchView?.maxWidth = Integer.MAX_VALUE

            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    adapter!!.filter.filter(query)
                    return false
                }
                override fun onQueryTextChange(query: String): Boolean {
                    adapter!!.filter.filter(query)
                    return false
                }
            })
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }
}
