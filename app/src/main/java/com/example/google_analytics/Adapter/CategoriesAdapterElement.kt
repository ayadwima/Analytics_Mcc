package com.example.googleanalyticsassignment.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googleanalyticsassignment.modle.MyCategory
import com.example.google_analytics.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.categories_item.view.*

class CategoriesAdapterElement(var activity: Context?, var data: MutableList<MyCategory>, var clickListener: onCategoryItemClickListener): RecyclerView.Adapter<CategoriesAdapterElement.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageCategoryElement  =itemView.categoryImage
        val nameCategoryElement  =itemView.categoryName

        fun initialize(data: MyCategory, action:onCategoryItemClickListener){

            Picasso.get().load(data.imageCategoryElement).into(imageCategoryElement)
            nameCategoryElement.text = data.nameCategoryElement
            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesAdapterElement.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.categories_item,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.initialize(data.get(position), clickListener)
    }
    interface onCategoryItemClickListener{
        fun onItemClick(data:MyCategory, position: Int)
    }
}