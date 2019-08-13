package com.example.kotlinapp.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kotlinapp.R
import com.example.kotlinapp.dtos.UserDto
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class CustomAdapter(private var items: ArrayList<UserDto>,var onClickListener:OnItemClickListener) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>(),Filterable {
    private var userSearchList: List<UserDto>? = null
    init {
        this.userSearchList=items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_item, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = userSearchList!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var userDto = userSearchList!![position]
        holder.txtPoints?.text = "Score: "+userDto.score.toString()
        holder.txtName?.text = userDto.name
        Picasso.get().load(userDto.image).transform(CropCircleTransformation()).into(holder.userImage)
        holder.bind(userDto,onClickListener)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    userSearchList = items
                }
                else {
                    val filteredList = ArrayList<UserDto>()
                    for (row in items) {

                        if (row.name!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    userSearchList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = userSearchList
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                userSearchList = filterResults.values as ArrayList<UserDto>
                notifyDataSetChanged()
            }
        }
    }
    public class ViewHolder(row: View?) : RecyclerView.ViewHolder(row as View),View.OnClickListener {
        override fun onClick(v: View?) {

        }

        var txtPoints: TextView? = null
        var txtName: TextView? = null
        var userImage: ImageView? = null
        var view: View? = null

        init {
            this.txtPoints = row?.findViewById<TextView>(R.id.label_number)
            this.txtName = row?.findViewById<TextView>(R.id.label_text)
            this.userImage = row?.findViewById<ImageView>(R.id.user_image)
            this.view = row?.findViewById<ConstraintLayout>(R.id.user_view)
        }
        fun bind( item:UserDto , listener: OnItemClickListener ) {

            view?.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(item: UserDto)
    }


}